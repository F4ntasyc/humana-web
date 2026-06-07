package com.humana.servlet;

import com.humana.dao.*;
import com.humana.model.Guru;
import com.humana.model.Murid;
import com.humana.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Servlet untuk manajemen profil pengguna (Guru & Murid).
 * URL Pattern: /profile/*
 *
 * <p>Adaptasi dari: editProfilController.js + feedbackController.js (getGuruRating, getMuridProfile)</p>
 * <p>Perbaikan: Profil guru/murid sekarang ada di satu servlet yang benar,
 * bukan di feedbackController seperti di mobile.</p>
 */
public class ProfileServlet extends HttpServlet {

    private final GuruDAO guruDAO = new GuruDAOImpl();
    private final MuridDAO muridDAO = new MuridDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/";

        if (pathInfo.startsWith("/guru/")) {
            getGuruProfile(req, resp, pathInfo.substring(6));
        } else if (pathInfo.startsWith("/murid/")) {
            getMuridProfile(req, resp, pathInfo.substring(7));
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
            case "/update-basic":
                updateBasic(req, resp);
                break;
            case "/update-academic":
                updateAcademic(req, resp);
                break;
            case "/update-availability":
                updateAvailability(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Ambil profil guru lengkap + rating (dihitung dari feedback).
     * Adaptasi dari feedbackController.getGuruRating() — dipindahkan ke sini karena ini profil, bukan feedback.
     */
    private void getGuruProfile(HttpServletRequest req, HttpServletResponse resp, String idStr)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            int idGuru = Integer.parseInt(idStr);
            Guru guru = guruDAO.findById(idGuru);

            if (guru == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"success\":false,\"message\":\"Guru tidak ditemukan.\"}");
                return;
            }

            // Hitung rating dari feedback (AVG via query, lebih efisien dari loop objek)
            double ratingKalkulasi = 0;
            String ratingSql = "SELECT AVG(f.rating) AS avg_rating FROM Feedback f "
                    + "JOIN Pemesanan p ON f.id_pemesanan = p.id_pemesanan WHERE p.id_guru = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(ratingSql)) {
                stmt.setInt(1, idGuru);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        ratingKalkulasi = rs.getDouble("avg_rating");
                    }
                }
            }

            out.print("{\"success\":true,\"data\":{"
                    + "\"id\":" + guru.getId()
                    + ",\"username\":\"" + escapeJson(guru.getUsername()) + "\""
                    + ",\"email\":\"" + escapeJson(guru.getEmail()) + "\""
                    + ",\"nama\":\"" + escapeJson(guru.getNamaUser()) + "\""
                    + ",\"no_telepon\":\"" + escapeJson(guru.getNoTelepon()) + "\""
                    + ",\"jenis_kelamin\":\"" + escapeJson(guru.getLabelJenisKelamin()) + "\""
                    + ",\"alamat\":\"" + escapeJson(guru.getAlamat()) + "\""
                    + ",\"rating\":" + String.format("%.2f", ratingKalkulasi)
                    + ",\"is_active\":" + guru.isActive()
                    + ",\"role\":\"GURU\"}}");

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"ID guru tidak valid.\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    /**
     * Ambil profil murid lengkap.
     * Adaptasi dari feedbackController.getMuridProfile() — dipindahkan ke sini.
     */
    private void getMuridProfile(HttpServletRequest req, HttpServletResponse resp, String idStr)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            int idMurid = Integer.parseInt(idStr);
            Murid murid = muridDAO.findById(idMurid);

            if (murid == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"success\":false,\"message\":\"Murid tidak ditemukan.\"}");
                return;
            }

            out.print("{\"success\":true,\"data\":{"
                    + "\"id\":" + murid.getId()
                    + ",\"username\":\"" + escapeJson(murid.getUsername()) + "\""
                    + ",\"email\":\"" + escapeJson(murid.getEmail()) + "\""
                    + ",\"nama\":\"" + escapeJson(murid.getNamaUser()) + "\""
                    + ",\"no_telepon\":\"" + escapeJson(murid.getNoTelepon()) + "\""
                    + ",\"jenis_kelamin\":\"" + escapeJson(murid.getLabelJenisKelamin()) + "\""
                    + ",\"alamat\":\"" + escapeJson(murid.getAlamat()) + "\""
                    + ",\"kelas\":" + murid.getKelas()
                    + ",\"jurusan\":\"" + escapeJson(murid.getJurusan()) + "\""
                    + ",\"jenjang\":\"" + escapeJson(murid.getJenjang()) + "\""
                    + ",\"kelas_jurusan\":\"" + escapeJson(murid.getKelasJurusan()) + "\""
                    + ",\"role\":\"MURID\"}}");

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"ID murid tidak valid.\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    /**
     * Update profil dasar (nama, username, telp, gender, alamat).
     * Adaptasi dari editProfilController.updateBasic().
     */
    private void updateBasic(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String idStr = req.getParameter("id");
        String email = req.getParameter("email");
        String name = req.getParameter("name");
        String username = req.getParameter("username");
        String phone = req.getParameter("phone");
        String gender = req.getParameter("gender");
        String domicile = req.getParameter("domicile");
        String role = req.getParameter("role");

        // Validasi format nomor telepon
        if (phone != null && !phone.isEmpty()) {
            try {
                Long.parseLong(phone);
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\":false,\"message\":\"Format nomor telepon harus berupa angka.\"}");
                return;
            }
        }

        // Normalisasi gender ke kode DB
        String genderDb = null;
        if (gender != null && !gender.isEmpty()) {
            String g = gender.trim().toLowerCase();
            if ("laki-laki".equals(g) || "l".equals(g)) genderDb = "L";
            else if ("perempuan".equals(g) || "p".equals(g)) genderDb = "P";
        }

        String userRole = (role != null) ? role.toLowerCase() : "murid";

        try {
            if ("guru".equals(userRole)) {
                String sql = "UPDATE Guru SET nama_guru = ?, username = ?, no_telepon = ?, "
                        + "jenis_kelamin = ?, alamat = ? WHERE id_guru = ? OR email_guru = ?";
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, name);
                    stmt.setString(2, username);
                    stmt.setString(3, phone);
                    stmt.setString(4, genderDb);
                    stmt.setString(5, domicile);
                    stmt.setString(6, idStr);
                    stmt.setString(7, email);
                    stmt.executeUpdate();
                }
            } else {
                String sql = "UPDATE Murid SET nama_murid = ?, username = ?, no_telepon = ?, "
                        + "jenis_kelamin = ?, alamat = ? WHERE id_murid = ? OR email = ?";
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, name);
                    stmt.setString(2, username);
                    stmt.setString(3, phone);
                    stmt.setString(4, genderDb);
                    stmt.setString(5, domicile);
                    stmt.setString(6, idStr);
                    stmt.setString(7, email);
                    stmt.executeUpdate();
                }
            }

            out.print("{\"success\":true,\"message\":\"Profil dasar berhasil diperbarui.\"}");

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"message\":\"Gagal memperbarui profil: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    /**
     * Update kelas & jurusan murid.
     * Adaptasi dari editProfilController.updateAcademic().
     */
    private void updateAcademic(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String email = req.getParameter("email");
        String major = req.getParameter("major");

        Integer kelasDb = null;
        String jurusanDb = null;

        if (major != null && major.contains("-")) {
            String[] parts = major.split("-");
            try { kelasDb = Integer.parseInt(parts[0].trim()); } catch (NumberFormatException ignored) {}
            if (parts.length > 1) {
                String raw = parts[1].trim();
                if (!raw.isEmpty() && !"(NULL)".equalsIgnoreCase(raw)) {
                    jurusanDb = raw;
                }
            }
        } else if (major != null && !major.isEmpty()) {
            try { kelasDb = Integer.parseInt(major.trim()); } catch (NumberFormatException ignored) {}
        }

        try {
            String sql = "UPDATE Murid SET kelas = ?, jurusan = ? WHERE email = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                if (kelasDb != null) stmt.setInt(1, kelasDb); else stmt.setNull(1, java.sql.Types.INTEGER);
                stmt.setString(2, jurusanDb);
                stmt.setString(3, email);
                stmt.executeUpdate();
            }

            out.print("{\"success\":true,\"message\":\"Profil akademik berhasil diperbarui.\"}");

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"message\":\"Gagal memperbarui profil akademik.\"}");
        }
    }

    /**
     * Toggle status ketersediaan guru (aktif/nonaktif).
     * Adaptasi dari editProfilController.updateAvailability().
     */
    private void updateAvailability(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String idGuru = req.getParameter("id_guru");
        String isActiveStr = req.getParameter("is_active");

        try {
            boolean isActive = "true".equalsIgnoreCase(isActiveStr) || "1".equals(isActiveStr);
            int statusDb = isActive ? 1 : 0;

            String sql = "UPDATE Guru SET is_active = ? WHERE id_guru = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, statusDb);
                stmt.setInt(2, Integer.parseInt(idGuru));
                stmt.executeUpdate();
            }

            out.print("{\"success\":true,\"message\":\"Status ketersediaan berhasil diubah menjadi "
                    + (isActive ? "Aktif" : "Nonaktif") + ".\"}");

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"message\":\"Gagal memperbarui status ketersediaan.\"}");
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r");
    }
}
