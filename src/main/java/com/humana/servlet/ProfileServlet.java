package com.humana.servlet;

import com.humana.dao.GuruDAO;
import com.humana.dao.MuridDAO;
import com.humana.model.Guru;
import com.humana.model.Murid;
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
 * Servlet untuk manajemen profil pengguna (Guru & Murid).
 * URL Pattern: /profile/*
 *
 * <p>MVC Controller — semua response melalui forward ke JSP atau redirect.
 * Tidak ada response JSON.</p>
 *
 * <p>Routes:
 * <ul>
 *   <li>GET  /profile              → tampilkan halaman profil</li>
 *   <li>POST /profile/update-basic → update data dasar, redirect</li>
 *   <li>POST /profile/update-academic    → update akademik (MURID), redirect</li>
 *   <li>POST /profile/update-availability → toggle aktif (GURU), redirect</li>
 * </ul>
 * </p>
 */
public class ProfileServlet extends HttpServlet {

    private final GuruDAO guruDAO = new GuruDAO();
    private final MuridDAO muridDAO = new MuridDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Proteksi halaman
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || "/".equals(pathInfo)) {
            showProfile(req, resp, session);
        } else {
            resp.sendRedirect(req.getContextPath() + "/profile");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Proteksi halaman
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/";

        switch (pathInfo) {
            case "/update-basic":
                updateBasic(req, resp, session);
                break;
            case "/update-academic":
                updateAcademic(req, resp, session);
                break;
            case "/update-availability":
                updateAvailability(req, resp, session);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/profile");
        }
    }

    // ======== GET: Tampilkan Profil ========

    /**
     * GET /profile — ambil data user, hitung rating (guru), forward ke profil.jsp
     */
    private void showProfile(HttpServletRequest req, HttpServletResponse resp, HttpSession session)
            throws ServletException, IOException {
        int userId = (int) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole");

        try {
            if ("GURU".equals(userRole)) {
                Guru guru = guruDAO.findById(userId);
                if (guru == null) {
                    req.setAttribute("error", "Data guru tidak ditemukan.");
                    req.getRequestDispatcher("/WEB-INF/views/profil.jsp").forward(req, resp);
                    return;
                }
                req.setAttribute("guru", guru);

                // Hitung AVG rating dari Feedback JOIN Pemesanan
                double ratingAvg = 0;
                String ratingSql = "SELECT AVG(f.rating) AS avg_rating FROM Feedback f "
                        + "JOIN Pemesanan p ON f.id_pemesanan = p.id_pemesanan WHERE p.id_guru = ?";
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(ratingSql)) {
                    stmt.setInt(1, userId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            ratingAvg = rs.getDouble("avg_rating");
                            if (rs.wasNull()) ratingAvg = 0;
                        }
                    }
                }
                req.setAttribute("rating", ratingAvg);

            } else if ("MURID".equals(userRole)) {
                Murid murid = muridDAO.findById(userId);
                if (murid == null) {
                    req.setAttribute("error", "Data murid tidak ditemukan.");
                    req.getRequestDispatcher("/WEB-INF/views/profil.jsp").forward(req, resp);
                    return;
                }
                req.setAttribute("murid", murid);
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Gagal memuat data profil.");
        }

        req.setAttribute("activePage", "profil");
        req.getRequestDispatcher("/WEB-INF/views/profil.jsp").forward(req, resp);
    }

    // ======== POST: Update Data Dasar ========

    /**
     * POST /profile/update-basic — update nama, username, noTelepon, jenisKelamin, alamat.
     * Berlaku untuk GURU dan MURID. Redirect ke /profile?sukses=1.
     */
    private void updateBasic(HttpServletRequest req, HttpServletResponse resp, HttpSession session)
            throws IOException {
        int userId = (int) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole");

        String nama = req.getParameter("nama");
        String username = req.getParameter("username");
        String noTelepon = req.getParameter("noTelepon");
        String jenisKelamin = req.getParameter("jenisKelamin");
        String alamat = req.getParameter("alamat");

        try {
            if ("GURU".equals(userRole)) {
                String sql = "UPDATE Guru SET nama_guru = ?, username = ?, no_telepon = ?, "
                        + "jenis_kelamin = ?, alamat = ? WHERE id_guru = ?";
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, nama);
                    stmt.setString(2, username);
                    stmt.setString(3, noTelepon);
                    stmt.setString(4, jenisKelamin);
                    stmt.setString(5, alamat);
                    stmt.setInt(6, userId);
                    stmt.executeUpdate();
                }
            } else {
                String sql = "UPDATE Murid SET nama_murid = ?, username = ?, no_telepon = ?, "
                        + "jenis_kelamin = ?, alamat = ? WHERE id_murid = ?";
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, nama);
                    stmt.setString(2, username);
                    stmt.setString(3, noTelepon);
                    stmt.setString(4, jenisKelamin);
                    stmt.setString(5, alamat);
                    stmt.setInt(6, userId);
                    stmt.executeUpdate();
                }
            }

            // Update session userName jika nama berubah
            if (nama != null && !nama.trim().isEmpty()) {
                session.setAttribute("userName", nama.trim());
            }

            resp.sendRedirect(req.getContextPath() + "/profile?sukses=1");

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/profile?error=Gagal+memperbarui+profil");
        }
    }

    // ======== POST: Update Akademik (MURID only) ========

    /**
     * POST /profile/update-academic — update kelas dan jurusan murid.
     * Hanya untuk role MURID. Redirect ke /profile?sukses=1.
     */
    private void updateAcademic(HttpServletRequest req, HttpServletResponse resp, HttpSession session)
            throws IOException {
        String userRole = (String) session.getAttribute("userRole");
        if (!"MURID".equals(userRole)) {
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String kelasStr = req.getParameter("kelas");
        String jenjang = req.getParameter("jenjang"); // SD, SMP, atau SMA — disimpan ke kolom jurusan

        try {
            int kelas = 0;
            if (kelasStr != null && !kelasStr.trim().isEmpty()) {
                kelas = Integer.parseInt(kelasStr.trim());
            }

            // jurusan di DB dipakai untuk menyimpan jenjang (SD/SMP/SMA)
            String sql = "UPDATE Murid SET kelas = ?, jurusan = ? WHERE id_murid = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, kelas);
                stmt.setString(2, jenjang != null ? jenjang.trim() : "");
                stmt.setInt(3, userId);
                stmt.executeUpdate();
            }

            resp.sendRedirect(req.getContextPath() + "/profile?sukses=1");

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/profile?error=Kelas+harus+berupa+angka");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/profile?error=Gagal+memperbarui+profil+akademik");
        }
    }

    // ======== POST: Update Ketersediaan (GURU only) ========

    /**
     * POST /profile/update-availability — toggle is_active guru.
     * Hanya untuk role GURU. Redirect ke /profile?sukses=1.
     */
    private void updateAvailability(HttpServletRequest req, HttpServletResponse resp, HttpSession session)
            throws IOException {
        String userRole = (String) session.getAttribute("userRole");
        if (!"GURU".equals(userRole)) {
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String isActiveStr = req.getParameter("isActive");

        try {
            int isActive = "1".equals(isActiveStr) ? 1 : 0;

            String sql = "UPDATE Guru SET is_active = ? WHERE id_guru = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, isActive);
                stmt.setInt(2, userId);
                stmt.executeUpdate();
            }

            resp.sendRedirect(req.getContextPath() + "/profile?sukses=1");

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/profile?error=Gagal+memperbarui+ketersediaan");
        }
    }
}
