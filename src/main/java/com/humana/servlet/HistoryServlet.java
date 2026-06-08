package com.humana.servlet;

import com.humana.dao.*;
import com.humana.model.Feedback;
import com.humana.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

/**
 * Servlet untuk riwayat sesi, jadwal aktif, dan feedback.
 * URL Pattern: /history/*
 *
 * <p>Adaptasi dari: historyController.js + feedbackController.js (submit/get feedback)</p>
 * <p>Perbaikan: Feedback dimerge ke sini karena feedback selalu terkait sesi yang sudah selesai.</p>
 */
public class HistoryServlet extends HttpServlet {

    private final FeedbackDAO feedbackDAO = new FeedbackDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/";

        // /history/feedback/{id_pemesanan}
        if (pathInfo.startsWith("/feedback/")) {
            getFeedbackByPemesanan(req, resp, pathInfo.substring(10));
            return;
        }

        // /history/active/{role}/{id}
        if (pathInfo.startsWith("/active/")) {
            String sub = pathInfo.substring(8); // "{role}/{id}"
            handleHistory(req, resp, sub, true);
            return;
        }

        // /history/{role}/{id}
        String sub = pathInfo.substring(1); // "{role}/{id}"
        if (sub.contains("/")) {
            handleHistory(req, resp, sub, false);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/";

        if ("/feedback".equals(pathInfo)) {
            submitFeedback(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Riwayat sesi (selesai/batal) atau jadwal aktif (dikonfirmasi/menunggu/berlangsung).
     * Adaptasi dari historyController.getHistory() dan getActiveSchedule().
     */
    private void handleHistory(HttpServletRequest req, HttpServletResponse resp, String sub, boolean isActive)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String[] parts = sub.split("/");
        if (parts.length < 2) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"Parameter role dan id wajib diisi.\"}");
            return;
        }

        String role = parts[0].toLowerCase();
        String idStr = parts[1];

        if (!"murid".equals(role) && !"guru".equals(role)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"Role harus bernilai Murid atau Guru.\"}");
            return;
        }

        int limit = 10;
        int offset = 0;
        try {
            if (req.getParameter("limit") != null) limit = Integer.parseInt(req.getParameter("limit"));
            if (req.getParameter("offset") != null) offset = Integer.parseInt(req.getParameter("offset"));
        } catch (NumberFormatException ignored) {}

        String whereClause = "murid".equals(role) ? "murid.id_murid" : "guru.id_guru";
        String statusFilter;
        String orderDir;

        if (isActive) {
            statusFilter = "('dikonfirmasi', 'menunggu konfirmasi', 'berlangsung')";
            orderDir = "ASC";
        } else {
            statusFilter = "('selesai', 'dibatalkan', 'dibatalkan_murid', 'dibatalkan_guru')";
            orderDir = "DESC";
        }

        try {
            int id = Integer.parseInt(idStr);

            String sql = "SELECT pemesanan.id_pemesanan, pemesanan.status_pemesanan, "
                    + "pemesanan.waktu_mulai, pemesanan.waktu_selesai, pemesanan.lokasi_sesi, "
                    + "murid.id_murid, murid.nama_murid, murid.email AS email_murid, murid.kelas AS kelas_murid, "
                    + "guru.id_guru, guru.nama_guru, guru.email_guru, "
                    + "materi.id_materi, materi.nama_materi, materi.kelas AS kelas_materi, materi.jurusan, "
                    + "mapel.id_mapel, mapel.nama_mapel, "
                    + "bayar.biaya_sesi, bayar.biaya_jarak, bayar.nominal, "
                    + "bayar.status_pembayaran, bayar.metode_pembayaran"
                    + (isActive ? "" : ", feedback.rating AS feedback_rating, feedback.komentar AS feedback_komentar")
                    + " FROM Pemesanan pemesanan "
                    + "JOIN Murid murid ON murid.id_murid = pemesanan.id_murid "
                    + (isActive ? "JOIN" : "JOIN") + " Guru guru ON guru.id_guru = pemesanan.id_guru "
                    + "LEFT JOIN Materi materi ON materi.id_materi = pemesanan.id_materi "
                    + "LEFT JOIN MataPelajaran mapel ON mapel.id_mapel = materi.id_mapel "
                    + "LEFT JOIN Pembayaran bayar ON bayar.id_pemesanan = pemesanan.id_pemesanan "
                    + (isActive ? "" : "LEFT JOIN Feedback feedback ON feedback.id_pemesanan = pemesanan.id_pemesanan ")
                    + "WHERE pemesanan.status_pemesanan IN " + statusFilter + " "
                    + "AND (" + whereClause + " = ?) "
                    + "ORDER BY pemesanan.waktu_mulai " + orderDir + " "
                    + "LIMIT ? OFFSET ?";

            StringBuilder dataJson = new StringBuilder("[");
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, id);
                stmt.setInt(2, limit);
                stmt.setInt(3, offset);

                try (ResultSet rs = stmt.executeQuery()) {
                    boolean first = true;
                    while (rs.next()) {
                        if (!first) dataJson.append(",");

                        int nominal = rs.getInt("nominal");
                        int biayaSesi = rs.getInt("biaya_sesi");
                        int biayaJarak = rs.getInt("biaya_jarak");

                        dataJson.append("{\"id_pemesanan\":").append(rs.getInt("id_pemesanan"))
                                .append(",\"status_pemesanan\":\"").append(escapeJson(rs.getString("status_pemesanan"))).append("\"")
                                .append(",\"waktu_mulai\":\"").append(rs.getString("waktu_mulai")).append("\"")
                                .append(",\"waktu_selesai\":\"").append(rs.getString("waktu_selesai")).append("\"")
                                .append(",\"lokasi_sesi\":\"").append(escapeJson(rs.getString("lokasi_sesi"))).append("\"")
                                .append(",\"murid\":{\"id_murid\":").append(rs.getInt("id_murid"))
                                .append(",\"nama_murid\":\"").append(escapeJson(rs.getString("nama_murid"))).append("\"")
                                .append(",\"email\":\"").append(escapeJson(rs.getString("email_murid"))).append("\"}")
                                .append(",\"guru\":{\"id_guru\":").append(rs.getInt("id_guru"))
                                .append(",\"nama_guru\":\"").append(escapeJson(rs.getString("nama_guru"))).append("\"}")
                                .append(",\"mata_pelajaran\":{\"id_mapel\":").append(rs.getInt("id_mapel"))
                                .append(",\"nama_mapel\":\"").append(escapeJson(rs.getString("nama_mapel"))).append("\"}")
                                .append(",\"materi\":{\"id_materi\":").append(rs.getInt("id_materi"))
                                .append(",\"nama_materi\":\"").append(escapeJson(rs.getString("nama_materi"))).append("\"}")
                                .append(",\"biaya_sesi\":").append(biayaSesi)
                                .append(",\"biaya_jarak\":").append(biayaJarak)
                                .append(",\"nominal\":").append(nominal)
                                .append(",\"status_pembayaran\":\"").append(escapeJson(rs.getString("status_pembayaran"))).append("\"");

                        if (!isActive) {
                            String fbRating = rs.getString("feedback_rating");
                            if (fbRating != null) {
                                dataJson.append(",\"feedback\":{\"rating\":").append(rs.getInt("feedback_rating"))
                                        .append(",\"komentar\":\"").append(escapeJson(rs.getString("feedback_komentar"))).append("\"}");
                            } else {
                                dataJson.append(",\"feedback\":null");
                            }
                        }

                        dataJson.append("}");
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
     * Submit feedback + recalculate dan update rating guru.
     * Adaptasi dari feedbackController.submitFeedback().
     * Perbaikan: Menggunakan AVG query di DB, bukan loop objek di memori.
     */
    private void submitFeedback(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String idPemesanan = req.getParameter("id_pemesanan");
        String komentar = req.getParameter("komentar");
        String ratingStr = req.getParameter("rating");

        if (idPemesanan == null || ratingStr == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"Data feedback tidak lengkap.\"}");
            return;
        }

        // Validasi SQL injection sederhana (dari mobile)
        if (komentar != null && komentar.matches(".*['\"/].*|.*--.*")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"Teks ulasan mengandung karakter ilegal.\"}");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            int pemesananId = Integer.parseInt(idPemesanan);
            int rating = Integer.parseInt(ratingStr);

            // 1. Cek feedback ganda
            Feedback existing = feedbackDAO.findByPemesananId(pemesananId);
            if (existing != null) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                out.print("{\"success\":false,\"message\":\"Feedback untuk sesi ini sudah pernah dikirim.\"}");
                return;
            }

            // 2. Simpan feedback
            Feedback fb = new Feedback();
            fb.setIdPemesanan(pemesananId);
            fb.setKomentar(komentar);
            fb.setRating(rating);
            feedbackDAO.insert(fb);

            // 3. Cari id_guru dari pemesanan
            int idGuru = 0;
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT id_guru FROM Pemesanan WHERE id_pemesanan = ?")) {
                stmt.setInt(1, pemesananId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) idGuru = rs.getInt("id_guru");
                }
            }

            // 4. Hitung ulang rating via AVG (lebih efisien daripada loop OOP di mobile)
            double ratingBaru = 0;
            if (idGuru > 0) {
                try (PreparedStatement stmt = conn.prepareStatement(
                        "SELECT AVG(f.rating) AS avg_rating FROM Feedback f "
                        + "JOIN Pemesanan p ON f.id_pemesanan = p.id_pemesanan WHERE p.id_guru = ?")) {
                    stmt.setInt(1, idGuru);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) ratingBaru = rs.getDouble("avg_rating");
                    }
                }

                // 5. Update rating di tabel guru
                try (PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE Guru SET rating = ? WHERE id_guru = ?")) {
                    stmt.setDouble(1, ratingBaru);
                    stmt.setInt(2, idGuru);
                    stmt.executeUpdate();
                }
            }

            resp.setStatus(HttpServletResponse.SC_CREATED);
            out.print("{\"success\":true,\"message\":\"Feedback berhasil dikirim.\","
                    + "\"ratingBaru\":" + String.format("%.2f", ratingBaru) + "}");

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"message\":\"Gagal mengirim feedback.\"}");
        }
    }

    /**
     * Ambil feedback yang sudah disimpan per pemesanan.
     * Adaptasi dari feedbackController.getFeedbackByPemesanan().
     */
    private void getFeedbackByPemesanan(HttpServletRequest req, HttpServletResponse resp, String idStr)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            int idPemesanan = Integer.parseInt(idStr);
            Feedback fb = feedbackDAO.findByPemesananId(idPemesanan);

            if (fb == null) {
                out.print("{\"success\":true,\"data\":null}");
            } else {
                out.print("{\"success\":true,\"data\":{"
                        + "\"id_feedback\":" + fb.getIdFeedback()
                        + ",\"id_pemesanan\":" + fb.getIdPemesanan()
                        + ",\"komentar\":\"" + escapeJson(fb.getKomentar()) + "\""
                        + ",\"rating\":" + fb.getRating() + "}}");
            }
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
