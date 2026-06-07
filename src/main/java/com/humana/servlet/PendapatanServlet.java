package com.humana.servlet;

import com.humana.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Servlet untuk statistik pendapatan guru.
 * URL Pattern: /pendapatan/*
 *
 * <p>Adaptasi dari: pendapatanController.js</p>
 */
public class PendapatanServlet extends HttpServlet {

    private static final String SESI_SELESAI_WHERE =
            "pm.id_guru = ? AND pm.status_pemesanan = 'selesai' AND p.nominal IS NOT NULL";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || "/".equals(pathInfo)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Parse: /{id_guru} atau /{id_guru}/riwayat
        String[] parts = pathInfo.substring(1).split("/");
        String idGuruStr = parts[0];

        if (parts.length == 1) {
            getPendapatan(req, resp, idGuruStr);
        } else if ("riwayat".equals(parts[1])) {
            getRiwayatPendapatan(req, resp, idGuruStr);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Summary pendapatan: total, bulan ini, sesi selesai, riwayat 10 terakhir.
     * Adaptasi dari pendapatanController.getPendapatan().
     */
    private void getPendapatan(HttpServletRequest req, HttpServletResponse resp, String idGuruStr)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            int idGuru = Integer.parseInt(idGuruStr);

            long totalPendapatan = 0;
            long bulanIni = 0;
            int sesiSelesai = 0;

            try (Connection conn = DBConnection.getConnection()) {
                // Total pendapatan
                String sql1 = "SELECT COALESCE(SUM(p.nominal), 0) AS total FROM Pembayaran p "
                        + "JOIN Pemesanan pm ON p.id_pemesanan = pm.id_pemesanan WHERE " + SESI_SELESAI_WHERE;
                try (PreparedStatement stmt = conn.prepareStatement(sql1)) {
                    stmt.setInt(1, idGuru);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) totalPendapatan = rs.getLong("total");
                    }
                }

                // Pendapatan bulan ini
                String sql2 = "SELECT COALESCE(SUM(p.nominal), 0) AS bulan_ini FROM Pembayaran p "
                        + "JOIN Pemesanan pm ON p.id_pemesanan = pm.id_pemesanan WHERE " + SESI_SELESAI_WHERE
                        + " AND ((p.tanggal_pembayaran IS NOT NULL AND MONTH(p.tanggal_pembayaran) = MONTH(CURDATE()) "
                        + "AND YEAR(p.tanggal_pembayaran) = YEAR(CURDATE())) "
                        + "OR (p.tanggal_pembayaran IS NULL AND MONTH(pm.waktu_selesai) = MONTH(CURDATE()) "
                        + "AND YEAR(pm.waktu_selesai) = YEAR(CURDATE())))";
                try (PreparedStatement stmt = conn.prepareStatement(sql2)) {
                    stmt.setInt(1, idGuru);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) bulanIni = rs.getLong("bulan_ini");
                    }
                }

                // Jumlah sesi selesai
                String sql3 = "SELECT COUNT(*) AS sesi_selesai FROM Pemesanan "
                        + "WHERE id_guru = ? AND status_pemesanan = 'selesai'";
                try (PreparedStatement stmt = conn.prepareStatement(sql3)) {
                    stmt.setInt(1, idGuru);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) sesiSelesai = rs.getInt("sesi_selesai");
                    }
                }

                // Riwayat 10 terakhir
                String sql4 = "SELECT pm.id_pemesanan, mp.nama_mapel, m.nama_materi, mu.nama_murid, "
                        + "DAYNAME(pm.waktu_mulai) AS hari, TIME_FORMAT(pm.waktu_mulai, '%H.%i') AS jam, "
                        + "p.nominal AS jumlah, p.status_pembayaran AS status "
                        + "FROM Pemesanan pm JOIN Pembayaran p ON pm.id_pemesanan = p.id_pemesanan "
                        + "JOIN Materi m ON pm.id_materi = m.id_materi "
                        + "JOIN MataPelajaran mp ON m.id_mapel = mp.id_mapel "
                        + "JOIN Murid mu ON pm.id_murid = mu.id_murid "
                        + "WHERE " + SESI_SELESAI_WHERE
                        + " AND ((p.tanggal_pembayaran IS NOT NULL AND MONTH(p.tanggal_pembayaran) = MONTH(CURDATE()) "
                        + "AND YEAR(p.tanggal_pembayaran) = YEAR(CURDATE())) "
                        + "OR (p.tanggal_pembayaran IS NULL AND MONTH(pm.waktu_selesai) = MONTH(CURDATE()) "
                        + "AND YEAR(pm.waktu_selesai) = YEAR(CURDATE()))) "
                        + "ORDER BY COALESCE(p.tanggal_pembayaran, pm.waktu_selesai) DESC LIMIT 10";

                StringBuilder riwayatJson = new StringBuilder("[");
                try (PreparedStatement stmt = conn.prepareStatement(sql4)) {
                    stmt.setInt(1, idGuru);
                    try (ResultSet rs = stmt.executeQuery()) {
                        boolean first = true;
                        while (rs.next()) {
                            if (!first) riwayatJson.append(",");
                            riwayatJson.append("{\"id_pemesanan\":").append(rs.getInt("id_pemesanan"))
                                    .append(",\"nama_mapel\":\"").append(escapeJson(rs.getString("nama_mapel"))).append("\"")
                                    .append(",\"nama_materi\":\"").append(escapeJson(rs.getString("nama_materi"))).append("\"")
                                    .append(",\"nama_murid\":\"").append(escapeJson(rs.getString("nama_murid"))).append("\"")
                                    .append(",\"hari\":\"").append(escapeJson(rs.getString("hari"))).append("\"")
                                    .append(",\"jam\":\"").append(escapeJson(rs.getString("jam"))).append("\"")
                                    .append(",\"jumlah\":").append(rs.getLong("jumlah"))
                                    .append(",\"status\":\"").append(escapeJson(rs.getString("status"))).append("\"")
                                    .append("}");
                            first = false;
                        }
                    }
                }
                riwayatJson.append("]");

                out.print("{\"success\":true,\"data\":{"
                        + "\"total_pendapatan\":" + totalPendapatan
                        + ",\"bulan_ini\":" + bulanIni
                        + ",\"sesi_selesai\":" + sesiSelesai
                        + ",\"riwayat\":" + riwayatJson
                        + "}}");
            }

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"ID guru tidak valid.\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    /**
     * Riwayat pendapatan dengan filter bulan/tahun.
     * Adaptasi dari pendapatanController.getRiwayatPendapatan().
     */
    private void getRiwayatPendapatan(HttpServletRequest req, HttpServletResponse resp, String idGuruStr)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String bulan = req.getParameter("bulan");
        String tahun = req.getParameter("tahun");

        try {
            int idGuru = Integer.parseInt(idGuruStr);

            StringBuilder sql = new StringBuilder(
                    "SELECT pm.id_pemesanan, mp.nama_mapel, m.nama_materi, mu.nama_murid, "
                    + "DAYNAME(pm.waktu_mulai) AS hari, TIME_FORMAT(pm.waktu_mulai, '%H.%i') AS jam, "
                    + "p.nominal AS jumlah, p.status_pembayaran AS status, p.tanggal_pembayaran "
                    + "FROM Pemesanan pm JOIN Pembayaran p ON pm.id_pemesanan = p.id_pemesanan "
                    + "JOIN Materi m ON pm.id_materi = m.id_materi "
                    + "JOIN MataPelajaran mp ON m.id_mapel = mp.id_mapel "
                    + "JOIN Murid mu ON pm.id_murid = mu.id_murid "
                    + "WHERE " + SESI_SELESAI_WHERE);

            java.util.List<Object> params = new java.util.ArrayList<>();
            params.add(idGuru);

            if (bulan != null && !bulan.isEmpty()) {
                sql.append(" AND ((p.tanggal_pembayaran IS NOT NULL AND MONTH(p.tanggal_pembayaran) = ?) "
                        + "OR (p.tanggal_pembayaran IS NULL AND MONTH(pm.waktu_selesai) = ?))");
                params.add(Integer.parseInt(bulan));
                params.add(Integer.parseInt(bulan));
            }
            if (tahun != null && !tahun.isEmpty()) {
                sql.append(" AND ((p.tanggal_pembayaran IS NOT NULL AND YEAR(p.tanggal_pembayaran) = ?) "
                        + "OR (p.tanggal_pembayaran IS NULL AND YEAR(pm.waktu_selesai) = ?))");
                params.add(Integer.parseInt(tahun));
                params.add(Integer.parseInt(tahun));
            }

            sql.append(" ORDER BY COALESCE(p.tanggal_pembayaran, pm.waktu_selesai) DESC");

            StringBuilder dataJson = new StringBuilder("[");
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

                for (int i = 0; i < params.size(); i++) {
                    stmt.setObject(i + 1, params.get(i));
                }

                try (ResultSet rs = stmt.executeQuery()) {
                    boolean first = true;
                    while (rs.next()) {
                        if (!first) dataJson.append(",");
                        dataJson.append("{\"id_pemesanan\":").append(rs.getInt("id_pemesanan"))
                                .append(",\"nama_mapel\":\"").append(escapeJson(rs.getString("nama_mapel"))).append("\"")
                                .append(",\"nama_materi\":\"").append(escapeJson(rs.getString("nama_materi"))).append("\"")
                                .append(",\"nama_murid\":\"").append(escapeJson(rs.getString("nama_murid"))).append("\"")
                                .append(",\"hari\":\"").append(escapeJson(rs.getString("hari"))).append("\"")
                                .append(",\"jam\":\"").append(escapeJson(rs.getString("jam"))).append("\"")
                                .append(",\"jumlah\":").append(rs.getLong("jumlah"))
                                .append(",\"status\":\"").append(escapeJson(rs.getString("status"))).append("\"")
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

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r");
    }
}
