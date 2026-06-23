package com.humana.servlet;

import com.humana.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Servlet untuk menangani proses pembayaran pesanan (POV Murid).
 * URL Pattern: /bayar/*
 */
public class PembayaranServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        String userRole = (String) session.getAttribute("userRole");
        if (!"MURID".equals(userRole)) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || "/".equals(pathInfo)) {
            tampilkanPembayaran(req, resp, session);
        } else {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        String pathInfo = req.getPathInfo();
        if ("/proses".equals(pathInfo)) {
            prosesPembayaran(req, resp, session);
        } else {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        }
    }

    private void tampilkanPembayaran(HttpServletRequest req, HttpServletResponse resp, HttpSession session)
            throws ServletException, IOException {
        int idMurid = (int) session.getAttribute("userId");
        String idStr = req.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/jadwal");
            return;
        }

        String sql = "SELECT p.*, murid.nama_murid, guru.nama_guru, materi.nama_materi, " +
                "mapel.nama_mapel, bayar.* " +
                "FROM Pemesanan p " +
                "JOIN Murid murid ON murid.id_murid = p.id_murid " +
                "LEFT JOIN Guru guru ON guru.id_guru = p.id_guru " +
                "LEFT JOIN Materi materi ON materi.id_materi = p.id_materi " +
                "LEFT JOIN MataPelajaran mapel ON mapel.id_mapel = materi.id_mapel " +
                "LEFT JOIN Pembayaran bayar ON bayar.id_pemesanan = p.id_pemesanan " +
                "WHERE p.id_pemesanan = ? AND p.id_murid = ? AND p.status_pemesanan = 'dikonfirmasi'";

        boolean found = false;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Integer.parseInt(idStr));
            stmt.setInt(2, idMurid);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    found = true;
                    req.setAttribute("idPemesanan", rs.getInt("id_pemesanan"));
                    req.setAttribute("namaGuru", rs.getString("nama_guru"));
                    req.setAttribute("namaMapel", rs.getString("nama_mapel"));
                    req.setAttribute("namaMateri", rs.getString("nama_materi"));
                    req.setAttribute("waktuMulai", rs.getTimestamp("waktu_mulai"));
                    req.setAttribute("waktuSelesai", rs.getTimestamp("waktu_selesai"));
                    req.setAttribute("lokasiSesi", rs.getString("lokasi_sesi"));
                    req.setAttribute("biayaSesi", rs.getInt("biaya_sesi"));
                    req.setAttribute("biayaJarak", rs.getInt("biaya_jarak"));
                    req.setAttribute("nominal", rs.getInt("nominal"));
                    req.setAttribute("statusPembayaran", rs.getString("status_pembayaran"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Terjadi kesalahan saat memuat detail pembayaran.");
        }

        if (!found) {
            resp.sendRedirect(req.getContextPath() + "/jadwal?error=Pembayaran+tidak+tersedia");
            return;
        }

        req.setAttribute("activePage", "jadwal");
        req.getRequestDispatcher("/WEB-INF/views/murid/pembayaran.jsp").forward(req, resp);
    }

    private void prosesPembayaran(HttpServletRequest req, HttpServletResponse resp, HttpSession session)
            throws IOException {
        int idMurid = (int) session.getAttribute("userId");
        String idPemesananStr = req.getParameter("idPemesanan");
        String metodePembayaran = req.getParameter("metodePembayaran");

        if (idPemesananStr == null || idPemesananStr.trim().isEmpty()
                || metodePembayaran == null || metodePembayaran.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/jadwal");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int idPemesanan = Integer.parseInt(idPemesananStr);

                String sqlCheck = "SELECT p.status_pemesanan, bay.status_pembayaran " +
                        "FROM Pemesanan p " +
                        "JOIN Pembayaran bay ON bay.id_pemesanan = p.id_pemesanan " +
                        "WHERE p.id_pemesanan = ? AND p.id_murid = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sqlCheck)) {
                    stmt.setInt(1, idPemesanan);
                    stmt.setInt(2, idMurid);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            resp.sendRedirect(req.getContextPath() + "/jadwal?error=Pesanan+tidak+ditemukan");
                            return;
                        }
                        if (!"dikonfirmasi".equals(rs.getString("status_pemesanan"))) {
                            conn.rollback();
                            resp.sendRedirect(req.getContextPath() + "/jadwal?error=Status+pesanan+tidak+valid");
                            return;
                        }
                        if (!"menunggu".equals(rs.getString("status_pembayaran"))) {
                            conn.rollback();
                            resp.sendRedirect(req.getContextPath() + "/jadwal?error=Pembayaran+sudah+diproses");
                            return;
                        }
                    }
                }

                String sqlPembayaran = "UPDATE Pembayaran SET status_pembayaran='lunas', " +
                        "metode_pembayaran=?, tanggal_pembayaran=NOW() " +
                        "WHERE id_pemesanan=? AND status_pembayaran='menunggu'";
                try (PreparedStatement stmt = conn.prepareStatement(sqlPembayaran)) {
                    stmt.setString(1, metodePembayaran);
                    stmt.setInt(2, idPemesanan);
                    int rows = stmt.executeUpdate();
                    if (rows == 0) {
                        conn.rollback();
                        resp.sendRedirect(req.getContextPath() + "/jadwal?error=Gagal+memproses+pembayaran");
                        return;
                    }
                }

                conn.commit();
                resp.sendRedirect(req.getContextPath() + "/jadwal?bayar=1&tab=aktif");
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/jadwal?error=Terjadi+kesalahan");
        }
    }
}
