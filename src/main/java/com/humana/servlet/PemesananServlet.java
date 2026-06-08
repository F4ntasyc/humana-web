package com.humana.servlet;

import com.humana.dao.*;
import com.humana.model.DraftPemesanan;
import com.humana.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

/**
 * Servlet untuk pemesanan sesi — POV Murid.
 * URL Pattern: /pemesanan/*
 *
 * <p>Adaptasi dari: pemesananController.js</p>
 * <p>Perbaikan: Draft pemesanan dimerge ke sini (bukan servlet terpisah).
 * Dropdown materi/mapel dipindahkan ke MateriServlet.</p>
 */
public class PemesananServlet extends HttpServlet {

    private final DraftPemesananDAO draftDAO = new DraftPemesananDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/";

        switch (pathInfo) {
            case "/cek-status":
                cekStatusPemesanan(req, resp);
                break;
            case "/get-draft":
                getDraftPemesanan(req, resp);
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
            case "/tambah":
                tambahPemesanan(req, resp);
                break;
            case "/batal":
                batalPemesanan(req, resp);
                break;
            case "/save-draft":
                saveDraftPemesanan(req, resp);
                break;
            case "/clear-draft":
                clearDraftPemesanan(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Tambah pemesanan baru (status awal: 'menunggu konfirmasi', id_guru null).
     * Adaptasi dari pemesananController.tambahPemesanan().
     */
    private void tambahPemesanan(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String idMuridStr = req.getParameter("id_murid");
        String idMateriStr = req.getParameter("id_materi");
        String waktuMulai = req.getParameter("waktu_mulai");
        String waktuSelesai = req.getParameter("waktu_selesai");
        String lokasiSesi = req.getParameter("lokasi_sesi");

        if (idMuridStr == null || idMateriStr == null || waktuMulai == null
                || waktuSelesai == null || lokasiSesi == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"Harap melengkapi seluruh form pemesanan.\"}");
            return;
        }

        try {
            String sql = "INSERT INTO Pemesanan (id_murid, id_guru, id_materi, status_pemesanan, "
                    + "waktu_mulai, waktu_selesai, lokasi_sesi) "
                    + "VALUES (?, NULL, ?, 'menunggu konfirmasi', ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                stmt.setInt(1, Integer.parseInt(idMuridStr));
                stmt.setInt(2, Integer.parseInt(idMateriStr));
                stmt.setString(3, waktuMulai);
                stmt.setString(4, waktuSelesai);
                stmt.setString(5, lokasiSesi);

                stmt.executeUpdate();
                int insertedId = 0;
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) insertedId = keys.getInt(1);
                }

                resp.setStatus(HttpServletResponse.SC_CREATED);
                out.print("{\"success\":true,\"message\":\"Pemesanan sesi berhasil disimpan!\","
                        + "\"id_pemesanan\":" + insertedId + "}");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"message\":\"Gagal menyimpan pemesanan.\"}");
        }
    }

    /**
     * Cek status pemesanan (+ info guru yang menerima).
     * Adaptasi dari pemesananController.cekStatusPemesananMurid().
     */
    private void cekStatusPemesanan(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String idPemesanan = req.getParameter("id_pemesanan");
        if (idPemesanan == null || idPemesanan.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"ID Pemesanan tidak disertakan.\"}");
            return;
        }

        try {
            String sql = "SELECT p.id_pemesanan, p.status_pemesanan, p.id_guru, g.nama_guru "
                    + "FROM Pemesanan p LEFT JOIN Guru g ON p.id_guru = g.id_guru "
                    + "WHERE p.id_pemesanan = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, Integer.parseInt(idPemesanan));
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int idGuru = rs.getInt("id_guru");
                        String guruJson = idGuru > 0
                                ? ",\"data_guru\":{\"id_guru\":" + idGuru + ",\"nama_guru\":\"" + escapeJson(rs.getString("nama_guru")) + "\"}"
                                : ",\"data_guru\":null";
                        out.print("{\"success\":true,\"status_pemesanan\":\""
                                + escapeJson(rs.getString("status_pemesanan")) + "\"" + guruJson + "}");
                    } else {
                        out.print("{\"success\":false,\"status_pemesanan\":null,\"message\":\"Pesanan tidak ditemukan.\"}");
                    }
                }
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    /**
     * Batalkan pemesanan (hanya jika status 'menunggu konfirmasi').
     * Adaptasi dari pemesananController.batalPemesanan().
     */
    private void batalPemesanan(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String idPemesanan = req.getParameter("id_pemesanan");
        if (idPemesanan == null || idPemesanan.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"Parameter id_pemesanan wajib diisi.\"}");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // Cek status dulu
            String checkSql = "SELECT status_pemesanan FROM Pemesanan WHERE id_pemesanan = ? LIMIT 1";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, Integer.parseInt(idPemesanan));
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (!rs.next()) {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.print("{\"success\":false,\"message\":\"Pemesanan tidak ditemukan.\"}");
                        return;
                    }
                    String status = rs.getString("status_pemesanan");
                    if (!"menunggu konfirmasi".equals(status)) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.print("{\"success\":false,\"message\":\"Pemesanan dengan status '"
                                + escapeJson(status) + "' tidak dapat dibatalkan.\"}");
                        return;
                    }
                }
            }

            // Hapus pemesanan
            String deleteSql = "DELETE FROM Pemesanan WHERE id_pemesanan = ?";
            try (PreparedStatement delStmt = conn.prepareStatement(deleteSql)) {
                delStmt.setInt(1, Integer.parseInt(idPemesanan));
                delStmt.executeUpdate();
            }

            out.print("{\"success\":true,\"message\":\"Pemesanan berhasil dibatalkan.\"}");

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"message\":\"Terjadi kesalahan saat membatalkan pemesanan.\"}");
        }
    }

    // ======== Draft Pemesanan ========

    /**
     * Simpan draft pemesanan (upsert).
     * Adaptasi dari pemesananController.saveDraftPemesanan().
     */
    private void saveDraftPemesanan(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String idMuridStr = req.getParameter("id_murid");
        String draftData = req.getParameter("draft_data");

        if (idMuridStr == null || idMuridStr.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"ID Murid diperlukan.\"}");
            return;
        }

        try {
            DraftPemesanan draft = new DraftPemesanan();
            draft.setIdMurid(Integer.parseInt(idMuridStr));
            draft.setDraftData(draftData);
            draftDAO.insertOrUpdate(draft);

            out.print("{\"success\":true,\"message\":\"Draft pemesanan berhasil disimpan.\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    /**
     * Ambil draft pemesanan.
     * Adaptasi dari pemesananController.getDraftPemesanan().
     */
    private void getDraftPemesanan(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String idMuridStr = req.getParameter("id_murid");
        if (idMuridStr == null || idMuridStr.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"ID Murid diperlukan.\"}");
            return;
        }

        try {
            DraftPemesanan draft = draftDAO.findByMuridId(Integer.parseInt(idMuridStr));
            if (draft == null) {
                out.print("{\"success\":true,\"data\":null}");
            } else {
                out.print("{\"success\":true,\"data\":" + draft.getDraftData()
                        + ",\"updated_at\":\"" + draft.getUpdatedAt() + "\"}");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    /**
     * Hapus draft pemesanan.
     * Adaptasi dari pemesananController.clearDraftPemesanan().
     */
    private void clearDraftPemesanan(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String idMuridStr = req.getParameter("id_murid");

        try {
            draftDAO.delete(Integer.parseInt(idMuridStr));
            out.print("{\"success\":true,\"message\":\"Draft berhasil dihapus.\"}");
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
