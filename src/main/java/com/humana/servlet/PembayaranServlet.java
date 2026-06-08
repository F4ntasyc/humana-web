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
 * Servlet untuk pembayaran dan detail sesi.
 * URL Pattern: /pembayaran/*
 *
 * <p>Adaptasi dari: bankerController.js</p>
 * <p>Di-skip: prosesPembayaranMidtrans (payment gateway pihak ketiga)</p>
 */
public class PembayaranServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/";

        if (pathInfo.startsWith("/detail/")) {
            getSesiDetail(req, resp, pathInfo.substring(8));
        } else if (pathInfo.startsWith("/status/")) {
            getStatusPembayaran(req, resp, pathInfo.substring(8));
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/";

        switch (pathInfo) {
            case "/bayar":
                bayarSimulasi(req, resp);
                break;
            case "/bayar-cod":
                prosesPembayaranCod(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Detail sesi lengkap: pemesanan + guru + murid + materi + pembayaran.
     * Adaptasi dari bankerController.getSesiDetail().
     */
    private void getSesiDetail(HttpServletRequest req, HttpServletResponse resp, String idStr)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            int id = Integer.parseInt(idStr);

            String sql = "SELECT p.id_pemesanan, p.status_pemesanan, p.waktu_mulai, p.waktu_selesai, "
                    + "p.lokasi_sesi, "
                    + "TIMESTAMPDIFF(MINUTE, p.waktu_mulai, p.waktu_selesai) AS durasi_menit, "
                    + "murid.id_murid, murid.nama_murid, murid.email AS email_murid, "
                    + "guru.id_guru, guru.nama_guru, guru.email_guru, "
                    + "materi.id_materi, materi.nama_materi, "
                    + "mapel.id_mapel, mapel.nama_mapel, "
                    + "bayar.id_pembayaran, bayar.biaya_sesi, bayar.biaya_jarak, "
                    + "bayar.metode_pembayaran, bayar.nominal, bayar.status_pembayaran, "
                    + "bayar.tanggal_pembayaran "
                    + "FROM Pemesanan p "
                    + "JOIN Murid murid ON murid.id_murid = p.id_murid "
                    + "LEFT JOIN Guru guru ON guru.id_guru = p.id_guru "
                    + "LEFT JOIN Materi materi ON materi.id_materi = p.id_materi "
                    + "LEFT JOIN MataPelajaran mapel ON mapel.id_mapel = materi.id_mapel "
                    + "LEFT JOIN Pembayaran bayar ON bayar.id_pemesanan = p.id_pemesanan "
                    + "WHERE p.id_pemesanan = ? LIMIT 1";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.print("{\"success\":false,\"message\":\"Detail sesi tidak ditemukan.\"}");
                        return;
                    }

                    out.print("{\"success\":true,\"data\":{"
                            + "\"id_pemesanan\":" + rs.getInt("id_pemesanan")
                            + ",\"status_pemesanan\":\"" + escapeJson(rs.getString("status_pemesanan")) + "\""
                            + ",\"waktu_mulai\":\"" + rs.getString("waktu_mulai") + "\""
                            + ",\"waktu_selesai\":\"" + rs.getString("waktu_selesai") + "\""
                            + ",\"lokasi_sesi\":\"" + escapeJson(rs.getString("lokasi_sesi")) + "\""
                            + ",\"durasi_menit\":" + rs.getInt("durasi_menit")
                            + ",\"murid\":{\"id_murid\":" + rs.getInt("id_murid")
                            + ",\"nama_murid\":\"" + escapeJson(rs.getString("nama_murid")) + "\""
                            + ",\"email\":\"" + escapeJson(rs.getString("email_murid")) + "\"}"
                            + ",\"guru\":{\"id_guru\":" + rs.getInt("id_guru")
                            + ",\"nama_guru\":\"" + escapeJson(rs.getString("nama_guru")) + "\""
                            + ",\"email_guru\":\"" + escapeJson(rs.getString("email_guru")) + "\"}"
                            + ",\"mata_pelajaran\":{\"id_mapel\":" + rs.getInt("id_mapel")
                            + ",\"nama_mapel\":\"" + escapeJson(rs.getString("nama_mapel")) + "\"}"
                            + ",\"materi\":{\"id_materi\":" + rs.getInt("id_materi")
                            + ",\"nama_materi\":\"" + escapeJson(rs.getString("nama_materi")) + "\"}"
                            + ",\"pembayaran\":{\"id_pembayaran\":" + rs.getInt("id_pembayaran")
                            + ",\"biaya_sesi\":" + rs.getInt("biaya_sesi")
                            + ",\"biaya_jarak\":" + rs.getInt("biaya_jarak")
                            + ",\"metode_pembayaran\":\"" + escapeJson(rs.getString("metode_pembayaran")) + "\""
                            + ",\"nominal\":" + rs.getInt("nominal")
                            + ",\"status_pembayaran\":\"" + escapeJson(rs.getString("status_pembayaran")) + "\""
                            + ",\"tanggal_pembayaran\":\"" + rs.getString("tanggal_pembayaran") + "\"}"
                            + "}}");
                }
            }

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"ID tidak valid.\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    /**
     * Cek status pembayaran berdasarkan id_pemesanan.
     * Adaptasi dari bankerController.getStatusPembayaran().
     */
    private void getStatusPembayaran(HttpServletRequest req, HttpServletResponse resp, String idStr)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            String sql = "SELECT status_pembayaran FROM Pembayaran WHERE id_pemesanan = ? LIMIT 1";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, Integer.parseInt(idStr));
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getString("status_pembayaran") != null) {
                        out.print("{\"success\":true,\"status_pembayaran\":\""
                                + escapeJson(rs.getString("status_pembayaran")) + "\"}");
                    } else {
                        out.print("{\"success\":true,\"status_pembayaran\":\"menunggu\"}");
                    }
                }
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    /**
     * Bayar simulasi: update status pemesanan → berlangsung, pembayaran → lunas.
     * Adaptasi dari bankerController.bayarSimulasi().
     */
    private void bayarSimulasi(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String idSesi = req.getParameter("id_sesi");
        if (idSesi == null || idSesi.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"Parameter id_sesi wajib diisi.\"}");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            int id = Integer.parseInt(idSesi);

            // Cek sesi dan pembayaran
            String checkSql = "SELECT p.id_pemesanan, bayar.id_pembayaran "
                    + "FROM Pemesanan p LEFT JOIN Pembayaran bayar ON bayar.id_pemesanan = p.id_pemesanan "
                    + "WHERE p.id_pemesanan = ? LIMIT 1";
            int idPembayaran = 0;
            try (PreparedStatement stmt = conn.prepareStatement(checkSql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.print("{\"success\":false,\"message\":\"Sesi tidak ditemukan.\"}");
                        return;
                    }
                    idPembayaran = rs.getInt("id_pembayaran");
                }
            }

            // Update pemesanan → berlangsung
            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE Pemesanan SET status_pemesanan = 'berlangsung' WHERE id_pemesanan = ?")) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }

            // Update pembayaran → lunas (jika ada)
            if (idPembayaran > 0) {
                try (PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE Pembayaran SET status_pembayaran = 'lunas', tanggal_pembayaran = CURDATE() "
                        + "WHERE id_pembayaran = ?")) {
                    stmt.setInt(1, idPembayaran);
                    stmt.executeUpdate();
                }
            }

            out.print("{\"success\":true,\"message\":\"Pembayaran berhasil dikonfirmasi.\","
                    + "\"data\":{\"id_sesi\":" + id + ",\"status_sekarang\":\"berlangsung\"}}");

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    /**
     * Proses pembayaran tunai (COD): pemesanan → berlangsung, pembayaran → lunas + tunai.
     * Adaptasi dari bankerController.prosesPembayaranCod().
     */
    private void prosesPembayaranCod(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String idSesi = req.getParameter("id_sesi");
        if (idSesi == null || idSesi.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"Parameter id_sesi wajib diisi.\"}");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            int id = Integer.parseInt(idSesi);

            // Update pemesanan → berlangsung
            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE Pemesanan SET status_pemesanan = 'berlangsung' WHERE id_pemesanan = ?")) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }

            // Cek record pembayaran
            String checkSql = "SELECT id_pembayaran FROM Pembayaran WHERE id_pemesanan = ? LIMIT 1";
            boolean adaPembayaran = false;
            try (PreparedStatement stmt = conn.prepareStatement(checkSql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    adaPembayaran = rs.next();
                }
            }

            if (adaPembayaran) {
                try (PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE Pembayaran SET metode_pembayaran = 'tunai', "
                        + "status_pembayaran = 'lunas', tanggal_pembayaran = CURDATE() "
                        + "WHERE id_pemesanan = ?")) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                }
            } else {
                // Fallback: buat record pembayaran baru
                try (PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO Pembayaran (id_pemesanan, metode_pembayaran, nominal, "
                        + "status_pembayaran, tanggal_pembayaran) "
                        + "VALUES (?, 'tunai', 34000, 'lunas', CURDATE())")) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                }
            }

            out.print("{\"success\":true,\"message\":\"Pemesanan tunai berhasil dicatat.\"}");

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r");
    }
}
