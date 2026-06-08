package com.humana.servlet;

import com.humana.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
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

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole");
        req.setAttribute("activePage", "dashboard");

        if ("MURID".equals(userRole)) {
            loadDashboardMurid(req, resp, userId);
        } else if ("GURU".equals(userRole)) {
            loadDashboardGuru(req, resp, userId);
        } else {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
        }
    }

    private void loadDashboardMurid(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws ServletException, IOException {
        int pesananAktif = 0;
        int sesiSelesai = 0;
        int sesiBerlangsung = 0;

        try (Connection conn = DBConnection.getConnection()) {
            String sqlCountAktif = "SELECT COUNT(*) FROM Pemesanan WHERE id_murid = ? AND status_pemesanan IN ('menunggu konfirmasi','dikonfirmasi','berlangsung')";
            try (PreparedStatement stmt = conn.prepareStatement(sqlCountAktif)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) pesananAktif = rs.getInt(1);
                }
            }

            String sqlCountSelesai = "SELECT COUNT(*) FROM Pemesanan WHERE id_murid = ? AND status_pemesanan = 'selesai'";
            try (PreparedStatement stmt = conn.prepareStatement(sqlCountSelesai)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) sesiSelesai = rs.getInt(1);
                }
            }

            String sqlCountBerlangsung = "SELECT COUNT(*) FROM Pemesanan WHERE id_murid = ? AND status_pemesanan = 'berlangsung'";
            try (PreparedStatement stmt = conn.prepareStatement(sqlCountBerlangsung)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) sesiBerlangsung = rs.getInt(1);
                }
            }

            String sqlJadwalTerdekat = "SELECT p.id_pemesanan, p.status_pemesanan, p.waktu_mulai, " +
                                       "m.nama_materi, guru.nama_guru " +
                                       "FROM Pemesanan p " +
                                       "JOIN Materi m ON m.id_materi = p.id_materi " +
                                       "LEFT JOIN Guru guru ON guru.id_guru = p.id_guru " +
                                       "WHERE p.id_murid = ? AND p.status_pemesanan IN ('menunggu konfirmasi','dikonfirmasi','berlangsung') " +
                                       "ORDER BY p.waktu_mulai ASC LIMIT 1";
            try (PreparedStatement stmt = conn.prepareStatement(sqlJadwalTerdekat)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        JadwalTerdekatDTO jt = new JadwalTerdekatDTO();
                        jt.idPemesanan = rs.getInt("id_pemesanan");
                        jt.statusPemesanan = rs.getString("status_pemesanan");
                        jt.waktuMulai = rs.getTimestamp("waktu_mulai");
                        jt.namaMateri = rs.getString("nama_materi");
                        jt.namaGuru = rs.getString("nama_guru");
                        req.setAttribute("jadwalTerdekat", jt);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        req.setAttribute("pesananAktif", pesananAktif);
        req.setAttribute("sesiSelesai", sesiSelesai);
        req.setAttribute("sesiBerlangsung", sesiBerlangsung);

        req.getRequestDispatcher("/WEB-INF/views/murid/dashboard.jsp").forward(req, resp);
    }

    private void loadDashboardGuru(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws ServletException, IOException {
        int permintaanMasuk = 0;
        int sesiAktif = 0;
        int sesiSelesai = 0;
        double rating = 0.0;
        List<PermintaanTerbaruDTO> permintaanTerbaru = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            String sqlCountPermintaan = "SELECT COUNT(*) FROM Pemesanan WHERE (id_guru = ? OR id_guru IS NULL) AND status_pemesanan = 'menunggu konfirmasi'";
            try (PreparedStatement stmt = conn.prepareStatement(sqlCountPermintaan)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) permintaanMasuk = rs.getInt(1);
                }
            }

            String sqlCountAktif = "SELECT COUNT(*) FROM Pemesanan WHERE id_guru = ? AND status_pemesanan IN ('dikonfirmasi','berlangsung')";
            try (PreparedStatement stmt = conn.prepareStatement(sqlCountAktif)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) sesiAktif = rs.getInt(1);
                }
            }

            String sqlCountSelesai = "SELECT COUNT(*) FROM Pemesanan WHERE id_guru = ? AND status_pemesanan = 'selesai'";
            try (PreparedStatement stmt = conn.prepareStatement(sqlCountSelesai)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) sesiSelesai = rs.getInt(1);
                }
            }

            String sqlRating = "SELECT rating FROM Guru WHERE id_guru = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlRating)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) rating = rs.getDouble("rating");
                }
            }

            String sqlTerbaru = "SELECT p.id_pemesanan, p.waktu_mulai, m.nama_materi, murid.nama_murid " +
                                "FROM Pemesanan p " +
                                "JOIN Materi m ON m.id_materi = p.id_materi " +
                                "JOIN Murid murid ON murid.id_murid = p.id_murid " +
                                "WHERE (p.id_guru = ? OR p.id_guru IS NULL) AND p.status_pemesanan = 'menunggu konfirmasi' " +
                                "ORDER BY p.id_pemesanan DESC LIMIT 3";
            try (PreparedStatement stmt = conn.prepareStatement(sqlTerbaru)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        PermintaanTerbaruDTO dto = new PermintaanTerbaruDTO();
                        dto.idPemesanan = rs.getInt("id_pemesanan");
                        dto.waktuMulai = rs.getTimestamp("waktu_mulai");
                        dto.namaMateri = rs.getString("nama_materi");
                        dto.namaMurid = rs.getString("nama_murid");
                        permintaanTerbaru.add(dto);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        req.setAttribute("permintaanMasuk", permintaanMasuk);
        req.setAttribute("sesiAktif", sesiAktif);
        req.setAttribute("sesiSelesai", sesiSelesai);
        req.setAttribute("rating", rating);
        req.setAttribute("permintaanTerbaru", permintaanTerbaru);

        req.getRequestDispatcher("/WEB-INF/views/guru/dashboard.jsp").forward(req, resp);
    }

    public static class JadwalTerdekatDTO {
        public int idPemesanan;
        public String statusPemesanan;
        public java.sql.Timestamp waktuMulai;
        public String namaMateri;
        public String namaGuru;

        public int getIdPemesanan() { return idPemesanan; }
        public String getStatusPemesanan() { return statusPemesanan; }
        public java.sql.Timestamp getWaktuMulai() { return waktuMulai; }
        public String getNamaMateri() { return namaMateri; }
        public String getNamaGuru() { return namaGuru; }
    }

    public static class PermintaanTerbaruDTO {
        public int idPemesanan;
        public java.sql.Timestamp waktuMulai;
        public String namaMateri;
        public String namaMurid;

        public int getIdPemesanan() { return idPemesanan; }
        public java.sql.Timestamp getWaktuMulai() { return waktuMulai; }
        public String getNamaMateri() { return namaMateri; }
        public String getNamaMurid() { return namaMurid; }
    }
}
