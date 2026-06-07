package com.humana.servlet;

import com.humana.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

/**
 * Servlet untuk matching guru-murid — POV Guru.
 * URL Pattern: /matching/*
 *
 * <p>Adaptasi dari: matchingController.js</p>
 * <p>Perbaikan:
 * <ul>
 *   <li>Kalkulasi Haversine tetap dipertahankan via method utilitas</li>
 *   <li>Dipisah dari PemesananServlet agar jelas POV guru vs murid</li>
 * </ul>
 * </p>
 */
public class MatchingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/";

        switch (pathInfo) {
            case "/permintaan-baru":
                getPermintaanBaru(req, resp);
                break;
            case "/sesi-dikonfirmasi":
                getSesiDikonfirmasi(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/";

        switch (pathInfo) {
            case "/terima":
                terimaPermintaanSesi(req, resp);
                break;
            case "/selesaikan":
                selesaikanSesi(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * List permintaan sesi baru yang cocok untuk guru tertentu.
     * Cek status aktif guru dulu, lalu query pemesanan tanpa guru yang materinya cocok.
     * Adaptasi dari matchingController.getPermintaanBaru().
     */
    private void getPermintaanBaru(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String idGuru = req.getParameter("id_guru");
        String latGuru = req.getParameter("lat_guru");
        String lngGuru = req.getParameter("lng_guru");

        if (idGuru == null || latGuru == null || lngGuru == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"Koordinat lokasi guru tidak terdeteksi.\"}");
            return;
        }

        try {
            double guruLat = Double.parseDouble(latGuru);
            double guruLng = Double.parseDouble(lngGuru);
            int guruId = Integer.parseInt(idGuru);

            try (Connection conn = DBConnection.getConnection()) {
                // Step 1: Cek status aktif guru
                String checkSql = "SELECT is_active FROM Guru WHERE id_guru = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setInt(1, guruId);
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (!rs.next()) {
                            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            out.print("{\"success\":false,\"message\":\"Data Guru tidak ditemukan.\"}");
                            return;
                        }
                        if (rs.getInt("is_active") == 0) {
                            out.print("{\"success\":true,\"is_active\":false,"
                                    + "\"message\":\"Status Anda saat ini sedang Nonaktif.\",\"data\":[]}");
                            return;
                        }
                    }
                }

                // Step 2: Query pemesanan yang cocok dengan materi guru
                String sql = "SELECT p.id_pemesanan, p.id_murid, p.waktu_mulai, p.waktu_selesai, "
                        + "p.lokasi_sesi, m.nama_murid, mat.nama_materi, mp.nama_mapel, mp.jenjang "
                        + "FROM Pemesanan p "
                        + "JOIN Murid m ON p.id_murid = m.id_murid "
                        + "JOIN Materi mat ON p.id_materi = mat.id_materi "
                        + "JOIN MataPelajaran mp ON mat.id_mapel = mp.id_mapel "
                        + "JOIN MateriGuru mg ON p.id_materi = mg.id_materi "
                        + "WHERE p.id_guru IS NULL AND mg.id_guru = ? "
                        + "AND LOWER(p.status_pemesanan) = 'menunggu konfirmasi' "
                        + "ORDER BY p.id_pemesanan DESC";

                StringBuilder dataJson = new StringBuilder("[");
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, guruId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        boolean first = true;
                        while (rs.next()) {
                            if (!first) dataJson.append(",");

                            // Hitung jarak Haversine
                            double jarakKm = 0;
                            String lokasi = rs.getString("lokasi_sesi");
                            if (lokasi != null) {
                                jarakKm = hitungJarakDariLokasi(lokasi, guruLat, guruLng);
                            }

                            dataJson.append("{\"id_pemesanan\":").append(rs.getInt("id_pemesanan"))
                                    .append(",\"id_murid\":").append(rs.getInt("id_murid"))
                                    .append(",\"nama_murid\":\"").append(escapeJson(rs.getString("nama_murid"))).append("\"")
                                    .append(",\"nama_materi\":\"").append(escapeJson(rs.getString("nama_materi"))).append("\"")
                                    .append(",\"nama_mapel\":\"").append(escapeJson(rs.getString("nama_mapel"))).append("\"")
                                    .append(",\"jenjang\":\"").append(escapeJson(rs.getString("jenjang"))).append("\"")
                                    .append(",\"waktu_mulai\":\"").append(rs.getString("waktu_mulai")).append("\"")
                                    .append(",\"waktu_selesai\":\"").append(rs.getString("waktu_selesai")).append("\"")
                                    .append(",\"lokasi_sesi\":\"").append(escapeJson(lokasi)).append("\"")
                                    .append(",\"jarak_km\":").append(String.format("%.2f", jarakKm))
                                    .append("}");
                            first = false;
                        }
                    }
                }
                dataJson.append("]");

                out.print("{\"success\":true,\"is_active\":true,\"data\":" + dataJson + "}");
            }

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    /**
     * Guru menerima permintaan sesi — update status + buat record pembayaran.
     * Adaptasi dari matchingController.terimaPermintaanSesi().
     */
    private void terimaPermintaanSesi(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String idPemesanan = req.getParameter("id_pemesanan");
        String idGuru = req.getParameter("id_guru");
        String biayaSesiStr = req.getParameter("biaya_sesi");
        String biayaJarakStr = req.getParameter("biaya_jarak");
        String totalStr = req.getParameter("total_pembayaran_final");

        if (idPemesanan == null || idGuru == null || totalStr == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"Data penerimaan tidak lengkap.\"}");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // 1. Update pemesanan: assign guru + status dikonfirmasi
            String updateSql = "UPDATE Pemesanan SET id_guru = ?, status_pemesanan = 'dikonfirmasi' "
                    + "WHERE id_pemesanan = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setInt(1, Integer.parseInt(idGuru));
                stmt.setInt(2, Integer.parseInt(idPemesanan));
                stmt.executeUpdate();
            }

            // 2. Insert pembayaran
            String insertSql = "INSERT INTO Pembayaran (id_pemesanan, biaya_sesi, biaya_jarak, "
                    + "metode_pembayaran, nominal, status_pembayaran) "
                    + "VALUES (?, ?, ?, 'menunggu', ?, 'menunggu')";
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                stmt.setInt(1, Integer.parseInt(idPemesanan));
                stmt.setInt(2, biayaSesiStr != null ? Integer.parseInt(biayaSesiStr) : 0);
                stmt.setInt(3, biayaJarakStr != null ? Integer.parseInt(biayaJarakStr) : 0);
                stmt.setInt(4, Integer.parseInt(totalStr));
                stmt.executeUpdate();
            }

            out.print("{\"success\":true,\"message\":\"Sesi berhasil diterima, tagihan pembayaran telah dibuat.\"}");

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    /**
     * List sesi yang sudah dikonfirmasi untuk guru tertentu.
     * Adaptasi dari matchingController.getSesiDikonfirmasi().
     */
    private void getSesiDikonfirmasi(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String idGuru = req.getParameter("id_guru");
        if (idGuru == null || idGuru.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"ID Guru tidak disediakan.\"}");
            return;
        }

        try {
            String sql = "SELECT p.id_pemesanan, p.status_pemesanan, p.id_murid, "
                    + "p.waktu_mulai, p.waktu_selesai, p.lokasi_sesi, "
                    + "m.nama_murid, mat.nama_materi, mp.nama_mapel, mp.jenjang, "
                    + "pem.biaya_sesi, pem.biaya_jarak, pem.metode_pembayaran, "
                    + "pem.nominal AS harga_total, pem.status_pembayaran "
                    + "FROM Pemesanan p "
                    + "JOIN Murid m ON p.id_murid = m.id_murid "
                    + "JOIN Materi mat ON p.id_materi = mat.id_materi "
                    + "JOIN MataPelajaran mp ON mat.id_mapel = mp.id_mapel "
                    + "LEFT JOIN Pembayaran pem ON p.id_pemesanan = pem.id_pemesanan "
                    + "WHERE p.id_guru = ? AND LOWER(p.status_pemesanan) IN ('dikonfirmasi', 'berlangsung') "
                    + "ORDER BY p.waktu_mulai ASC";

            StringBuilder dataJson = new StringBuilder("[");
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, Integer.parseInt(idGuru));
                try (ResultSet rs = stmt.executeQuery()) {
                    boolean first = true;
                    while (rs.next()) {
                        if (!first) dataJson.append(",");

                        int biayaSesi = rs.getInt("biaya_sesi");
                        int biayaJarak = rs.getInt("biaya_jarak");
                        int nominal = rs.getInt("harga_total");
                        int hargaTotal = nominal > 0 ? nominal : biayaSesi + biayaJarak;

                        dataJson.append("{\"id_pemesanan\":").append(rs.getInt("id_pemesanan"))
                                .append(",\"status_pemesanan\":\"").append(escapeJson(rs.getString("status_pemesanan"))).append("\"")
                                .append(",\"id_murid\":").append(rs.getInt("id_murid"))
                                .append(",\"nama_murid\":\"").append(escapeJson(rs.getString("nama_murid"))).append("\"")
                                .append(",\"nama_materi\":\"").append(escapeJson(rs.getString("nama_materi"))).append("\"")
                                .append(",\"nama_mapel\":\"").append(escapeJson(rs.getString("nama_mapel"))).append("\"")
                                .append(",\"jenjang\":\"").append(escapeJson(rs.getString("jenjang"))).append("\"")
                                .append(",\"waktu_mulai\":\"").append(rs.getString("waktu_mulai")).append("\"")
                                .append(",\"waktu_selesai\":\"").append(rs.getString("waktu_selesai")).append("\"")
                                .append(",\"lokasi_sesi\":\"").append(escapeJson(rs.getString("lokasi_sesi"))).append("\"")
                                .append(",\"biaya_sesi\":").append(biayaSesi)
                                .append(",\"biaya_jarak\":").append(biayaJarak)
                                .append(",\"harga_total\":").append(hargaTotal)
                                .append(",\"status_pembayaran\":\"").append(escapeJson(rs.getString("status_pembayaran"))).append("\"")
                                .append(",\"metode_pembayaran\":\"").append(escapeJson(rs.getString("metode_pembayaran"))).append("\"")
                                .append("}");
                        first = false;
                    }
                }
            }
            dataJson.append("]");

            out.print("{\"success\":true,\"data\":" + dataJson + "}");

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    /**
     * Guru menyelesaikan sesi.
     * Adaptasi dari matchingController.selesaikanSesi().
     */
    private void selesaikanSesi(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String idPemesanan = req.getParameter("id_pemesanan");
        if (idPemesanan == null || idPemesanan.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"id_pemesanan diperlukan.\"}");
            return;
        }

        try {
            String sql = "UPDATE Pemesanan SET status_pemesanan = 'selesai' WHERE id_pemesanan = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, Integer.parseInt(idPemesanan));
                stmt.executeUpdate();
            }
            out.print("{\"success\":true,\"message\":\"Sesi berhasil diselesaikan.\"}");

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    // ======== Utilitas ========

    /**
     * Rumus Haversine untuk menghitung jarak antara 2 titik koordinat (dalam KM).
     * Dipindahkan dari matchingController.hitungJarak() (function-level) menjadi static method.
     */
    private static double hitungJarak(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371; // Radius bumi dalam km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * Parse lokasi_sesi string (format: "lat,lng|alamat" atau "lat,lng") dan hitung jarak.
     */
    private double hitungJarakDariLokasi(String lokasi, double guruLat, double guruLng) {
        try {
            String coordPart = lokasi.contains("|") ? lokasi.split("\\|")[0] : lokasi;
            String[] coords = coordPart.split(",");
            double muridLat = Double.parseDouble(coords[0].trim());
            double muridLng = Double.parseDouble(coords[1].trim());
            return hitungJarak(guruLat, guruLng, muridLat, muridLng);
        } catch (Exception e) {
            return 0;
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r");
    }
}
