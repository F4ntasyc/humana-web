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
import java.util.ArrayList;
import java.util.List;

/**
 * Halaman pendapatan guru (HTML) — data dimuat server-side.
 * URL: /guru/pendapatan
 */
public class PendapatanServlet extends HttpServlet {

    private static final String SESI_SELESAI_WHERE =
            "pm.id_guru = ? AND pm.status_pemesanan = 'selesai' AND p.nominal IS NOT NULL";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }
        if (!"GURU".equals(session.getAttribute("userRole"))) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        int idGuru = (int) session.getAttribute("userId");
        long totalPendapatan = 0;
        long bulanIni = 0;
        int sesiSelesai = 0;
        List<RiwayatDTO> riwayat = new ArrayList<>();
        boolean dbError = false;

        try (Connection conn = DBConnection.getConnection()) {
            String sql1 = "SELECT COALESCE(SUM(p.nominal), 0) AS total FROM Pembayaran p "
                    + "JOIN Pemesanan pm ON p.id_pemesanan = pm.id_pemesanan WHERE " + SESI_SELESAI_WHERE
                    + " AND p.status_pembayaran = 'lunas'";
            try (PreparedStatement stmt = conn.prepareStatement(sql1)) {
                stmt.setInt(1, idGuru);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) totalPendapatan = rs.getLong("total");
                }
            }

            String sql2 = "SELECT COALESCE(SUM(p.nominal), 0) AS bulan_ini FROM Pembayaran p "
                    + "JOIN Pemesanan pm ON p.id_pemesanan = pm.id_pemesanan WHERE " + SESI_SELESAI_WHERE
                    + " AND p.status_pembayaran = 'lunas' "
                    + "AND ((p.tanggal_pembayaran IS NOT NULL AND MONTH(p.tanggal_pembayaran) = MONTH(CURDATE()) "
                    + "AND YEAR(p.tanggal_pembayaran) = YEAR(CURDATE())) "
                    + "OR (p.tanggal_pembayaran IS NULL AND MONTH(pm.waktu_selesai) = MONTH(CURDATE()) "
                    + "AND YEAR(pm.waktu_selesai) = YEAR(CURDATE())))";
            try (PreparedStatement stmt = conn.prepareStatement(sql2)) {
                stmt.setInt(1, idGuru);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) bulanIni = rs.getLong("bulan_ini");
                }
            }

            String sql3 = "SELECT COUNT(*) AS cnt FROM Pemesanan WHERE id_guru = ? AND status_pemesanan = 'selesai'";
            try (PreparedStatement stmt = conn.prepareStatement(sql3)) {
                stmt.setInt(1, idGuru);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) sesiSelesai = rs.getInt("cnt");
                }
            }

            String sql4 = "SELECT pm.id_pemesanan, mp.nama_mapel, m.nama_materi, mu.nama_murid, "
                    + "pm.waktu_mulai, p.nominal, p.status_pembayaran "
                    + "FROM Pemesanan pm JOIN Pembayaran p ON pm.id_pemesanan = p.id_pemesanan "
                    + "JOIN Materi m ON pm.id_materi = m.id_materi "
                    + "JOIN MataPelajaran mp ON m.id_mapel = mp.id_mapel "
                    + "JOIN Murid mu ON pm.id_murid = mu.id_murid "
                    + "WHERE " + SESI_SELESAI_WHERE + " AND p.status_pembayaran = 'lunas' "
                    + "ORDER BY COALESCE(p.tanggal_pembayaran, pm.waktu_selesai) DESC LIMIT 10";
            try (PreparedStatement stmt = conn.prepareStatement(sql4)) {
                stmt.setInt(1, idGuru);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        RiwayatDTO dto = new RiwayatDTO();
                        dto.idPemesanan = rs.getInt("id_pemesanan");
                        dto.namaMapel = rs.getString("nama_mapel");
                        dto.namaMateri = rs.getString("nama_materi");
                        dto.namaMurid = rs.getString("nama_murid");
                        dto.waktuMulai = rs.getTimestamp("waktu_mulai");
                        dto.nominal = rs.getLong("nominal");
                        dto.statusPembayaran = rs.getString("status_pembayaran");
                        riwayat.add(dto);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            dbError = true;
        }

        req.setAttribute("totalPendapatan", totalPendapatan);
        req.setAttribute("bulanIni", bulanIni);
        req.setAttribute("sesiSelesai", sesiSelesai);
        req.setAttribute("riwayatPendapatan", riwayat);
        req.setAttribute("activePage", "pendapatan");
        if (dbError) {
            req.setAttribute("error", "Gagal memuat data pendapatan.");
        }
        req.getRequestDispatcher("/WEB-INF/views/guru/pendapatan.jsp").forward(req, resp);
    }

    public static class RiwayatDTO {
        public int idPemesanan;
        public String namaMapel;
        public String namaMateri;
        public String namaMurid;
        public java.sql.Timestamp waktuMulai;
        public long nominal;
        public String statusPembayaran;

        public int getIdPemesanan() { return idPemesanan; }
        public String getNamaMapel() { return namaMapel; }
        public String getNamaMateri() { return namaMateri; }
        public String getNamaMurid() { return namaMurid; }
        public java.sql.Timestamp getWaktuMulai() { return waktuMulai; }
        public long getNominal() { return nominal; }
        public String getStatusPembayaran() { return statusPembayaran; }
    }
}
