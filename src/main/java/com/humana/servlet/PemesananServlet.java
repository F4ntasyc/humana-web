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
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet untuk pemesanan sesi — POV Murid.
 * URL Pattern: /pesan/*
 */
public class PemesananServlet extends HttpServlet {

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
            tampilkanFormPesan(req, resp);
        } else if ("/menunggu".equals(pathInfo)) {
            tampilkanMenungguGuru(req, resp, session);
        } else {
            resp.sendRedirect(req.getContextPath() + "/pesan");
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
        if ("/tambah".equals(pathInfo)) {
            tambahPemesanan(req, resp);
        } else if ("/batal".equals(pathInfo)) {
            batalPemesanan(req, resp, session);
        } else {
            resp.sendRedirect(req.getContextPath() + "/pesan");
        }
    }

    private void tampilkanFormPesan(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String idMateri = req.getParameter("idMateri");

        List<MateriDTO> daftarMateri = new ArrayList<>();
        String sqlMateri = "SELECT m.id_materi, m.nama_materi, m.kelas, mp.nama_mapel " +
                "FROM Materi m JOIN MataPelajaran mp ON m.id_mapel = mp.id_mapel";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlMateri);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                MateriDTO dto = new MateriDTO();
                dto.idMateri = rs.getInt("id_materi");
                dto.namaMateri = rs.getString("nama_materi");
                dto.kelas = rs.getInt("kelas");
                dto.namaMapel = rs.getString("nama_mapel");
                daftarMateri.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Gagal memuat daftar materi.");
        }

        req.setAttribute("daftarMateri", daftarMateri);
        if (idMateri != null) {
            req.setAttribute("selectedMateri", idMateri);
        }
        req.setAttribute("activePage", "pesan");
        req.getRequestDispatcher("/WEB-INF/views/murid/pesan.jsp").forward(req, resp);
    }

    /**
     * Halaman menunggu konfirmasi guru — meta refresh setiap 3 detik (tanpa polling JS).
     */
    private void tampilkanMenungguGuru(HttpServletRequest req, HttpServletResponse resp, HttpSession session)
            throws ServletException, IOException {
        int idMurid = (int) session.getAttribute("userId");
        String idStr = req.getParameter("id");
        if (idStr == null || idStr.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/jadwal");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT p.id_pemesanan, p.status_pemesanan, p.waktu_mulai, p.waktu_selesai, " +
                    "m.nama_materi, mp.nama_mapel, g.nama_guru " +
                    "FROM Pemesanan p " +
                    "JOIN Materi m ON m.id_materi = p.id_materi " +
                    "JOIN MataPelajaran mp ON mp.id_mapel = m.id_mapel " +
                    "LEFT JOIN Guru g ON g.id_guru = p.id_guru " +
                    "WHERE p.id_pemesanan = ? AND p.id_murid = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, Integer.parseInt(idStr));
                stmt.setInt(2, idMurid);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        resp.sendRedirect(req.getContextPath() + "/jadwal?error=Pesanan+tidak+ditemukan");
                        return;
                    }
                    String status = rs.getString("status_pemesanan");
                    if ("dikonfirmasi".equals(status)) {
                        resp.sendRedirect(req.getContextPath() + "/bayar?id=" + idStr);
                        return;
                    }
                    if ("dibatalkan".equals(status)) {
                        resp.sendRedirect(req.getContextPath() + "/pesan?error=Permintaan+dibatalkan");
                        return;
                    }
                    if (!"menunggu konfirmasi".equals(status)) {
                        resp.sendRedirect(req.getContextPath() + "/jadwal");
                        return;
                    }
                    req.setAttribute("idPemesanan", rs.getInt("id_pemesanan"));
                    req.setAttribute("namaMateri", rs.getString("nama_materi"));
                    req.setAttribute("namaMapel", rs.getString("nama_mapel"));
                    req.setAttribute("waktuMulai", rs.getTimestamp("waktu_mulai"));
                    req.setAttribute("waktuSelesai", rs.getTimestamp("waktu_selesai"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/jadwal?error=Terjadi+kesalahan");
            return;
        }

        req.setAttribute("activePage", "pesan");
        resp.setHeader("Refresh", "3;url=" + req.getContextPath() + "/pesan/menunggu?id=" + idStr);
        req.getRequestDispatcher("/WEB-INF/views/murid/menunggu-guru.jsp").forward(req, resp);
    }

    private void tambahPemesanan(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String userRole = (String) session.getAttribute("userRole");
        if (!"MURID".equals(userRole)) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        int idMurid = (int) session.getAttribute("userId");
        String idMateriStr = req.getParameter("idMateri");
        String waktuMulaiStr = req.getParameter("waktuMulai");
        String waktuSelesaiStr = req.getParameter("waktuSelesai");
        String lokasiSesi = req.getParameter("lokasiSesi");

        if (idMateriStr == null || idMateriStr.trim().isEmpty()
                || waktuMulaiStr == null || waktuMulaiStr.trim().isEmpty()
                || waktuSelesaiStr == null || waktuSelesaiStr.trim().isEmpty()
                || lokasiSesi == null || lokasiSesi.trim().isEmpty()) {
            req.setAttribute("error", "Harap isi semua field yang diperlukan (materi, jadwal, dan lokasi).");
            tampilkanFormPesan(req, resp);
            return;
        }

        try {
            LocalDateTime mulai = LocalDateTime.parse(waktuMulaiStr);
            LocalDateTime selesai = LocalDateTime.parse(waktuSelesaiStr);

            if (!selesai.isAfter(mulai)) {
                req.setAttribute("error", "Waktu selesai harus setelah waktu mulai.");
                tampilkanFormPesan(req, resp);
                return;
            }

            long menit = Duration.between(mulai, selesai).toMinutes();
            if (menit < 60) {
                req.setAttribute("error", "Durasi sesi minimal 1 jam.");
                tampilkanFormPesan(req, resp);
                return;
            }

            int idPemesananBaru;
            try (Connection conn = DBConnection.getConnection()) {
                String sqlPemesanan = "INSERT INTO Pemesanan (id_murid, id_materi, status_pemesanan, " +
                        "waktu_mulai, waktu_selesai, lokasi_sesi) " +
                        "VALUES (?, ?, 'menunggu konfirmasi', ?, ?, ?)";

                try (PreparedStatement stmt = conn.prepareStatement(sqlPemesanan, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, idMurid);
                    stmt.setInt(2, Integer.parseInt(idMateriStr));
                    stmt.setTimestamp(3, Timestamp.valueOf(mulai));
                    stmt.setTimestamp(4, Timestamp.valueOf(selesai));
                    stmt.setString(5, lokasiSesi.trim());
                    stmt.executeUpdate();

                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new RuntimeException("Gagal mendapatkan ID pemesanan baru.");
                        }
                        idPemesananBaru = rs.getInt(1);
                    }
                }
            }

            resp.sendRedirect(req.getContextPath() + "/pesan/menunggu?id=" + idPemesananBaru);

        } catch (DateTimeParseException e) {
            req.setAttribute("error", "Format waktu tidak valid.");
            tampilkanFormPesan(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Terjadi kesalahan sistem saat menyimpan pemesanan.");
            tampilkanFormPesan(req, resp);
        }
    }

    private void batalPemesanan(HttpServletRequest req, HttpServletResponse resp, HttpSession session)
            throws IOException {
        int idMurid = (int) session.getAttribute("userId");
        String idPemesananStr = req.getParameter("idPemesanan");
        if (idPemesananStr == null || idPemesananStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/jadwal");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String sqlCheck = "SELECT status_pemesanan, id_murid FROM Pemesanan WHERE id_pemesanan = ?";
                String status = null;
                int pemesananMurid = -1;
                try (PreparedStatement stmt = conn.prepareStatement(sqlCheck)) {
                    stmt.setInt(1, Integer.parseInt(idPemesananStr));
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            status = rs.getString("status_pemesanan");
                            pemesananMurid = rs.getInt("id_murid");
                        }
                    }
                }

                if (pemesananMurid != idMurid) {
                    resp.sendRedirect(req.getContextPath() + "/jadwal?error=Akses+ditolak");
                    return;
                }

                if ("menunggu konfirmasi".equals(status)) {
                    int idPemesanan = Integer.parseInt(idPemesananStr);
                    try (PreparedStatement stmt = conn.prepareStatement(
                            "DELETE FROM Pembayaran WHERE id_pemesanan = ?")) {
                        stmt.setInt(1, idPemesanan);
                        stmt.executeUpdate();
                    }
                    try (PreparedStatement stmt = conn.prepareStatement(
                            "DELETE FROM Pemesanan WHERE id_pemesanan = ? AND id_murid = ? AND status_pemesanan = 'menunggu konfirmasi'")) {
                        stmt.setInt(1, idPemesanan);
                        stmt.setInt(2, idMurid);
                        stmt.executeUpdate();
                    }
                    conn.commit();
                    resp.sendRedirect(req.getContextPath() + "/jadwal?batal=1");
                } else {
                    conn.rollback();
                    resp.sendRedirect(req.getContextPath() + "/jadwal?error=Status+tidak+valid+untuk+dibatalkan");
                }
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

    public static class MateriDTO {
        public int idMateri;
        public String namaMateri;
        public int kelas;
        public String namaMapel;

        public int getIdMateri() { return idMateri; }
        public String getNamaMateri() { return namaMateri; }
        public int getKelas() { return kelas; }
        public String getNamaMapel() { return namaMapel; }
    }
}
