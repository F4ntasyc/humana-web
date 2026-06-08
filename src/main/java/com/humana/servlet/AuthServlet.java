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
 * Servlet untuk autentikasi dan registrasi pengguna.
 * URL Pattern: /auth/*
 *
 * <p>MVC Controller — semua response melalui forward ke JSP atau redirect.
 * Tidak ada response JSON.</p>
 *
 * <p>Routes:
 * <ul>
 *   <li>GET  /auth/login    → tampilkan halaman login</li>
 *   <li>GET  /auth/register → tampilkan halaman registrasi</li>
 *   <li>GET  /auth/logout   → invalidate session, redirect ke login</li>
 *   <li>POST /auth/login    → proses login, redirect ke dashboard</li>
 *   <li>POST /auth/register → proses registrasi, redirect ke login</li>
 * </ul>
 * </p>
 */
public class AuthServlet extends HttpServlet {

    private final GuruDAO guruDAO = new GuruDAO();
    private final MuridDAO muridDAO = new MuridDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/";

        switch (pathInfo) {
            case "/login":
                showLogin(req, resp);
                break;
            case "/register":
                showRegister(req, resp);
                break;
            case "/logout":
                logout(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/auth/login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/";

        switch (pathInfo) {
            case "/login":
                processLogin(req, resp);
                break;
            case "/register":
                processRegister(req, resp);
                break;
            case "/logout":
                logout(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/auth/login");
        }
    }

    // ======== GET Handlers ========

    /**
     * GET /auth/login — tampilkan halaman login.
     * Jika sudah login (session ada), redirect ke /dashboard.
     */
    private void showLogin(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
    }

    /**
     * GET /auth/register — tampilkan halaman registrasi.
     */
    private void showRegister(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
    }

    /**
     * GET /auth/logout — invalidate session, redirect ke login.
     */
    private void logout(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/auth/login");
    }

    // ======== POST Handlers ========

    /**
     * POST /auth/login — proses login via UNION query Guru + Murid.
     * Jika berhasil: set session, redirect ke /dashboard.
     * Jika gagal: set error attribute, forward ke login.jsp.
     */
    private void processLogin(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        // Validasi input kosong
        if (email == null || email.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            req.setAttribute("error", "Email dan password wajib diisi.");
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            return;
        }

        email = email.trim();
        password = password.trim();

        String sql = "SELECT id_guru AS id, nama_guru AS nama, email_guru AS email, password, "
                + "'GURU' AS role, username FROM Guru WHERE email_guru = ? OR username = ? "
                + "UNION ALL "
                + "SELECT id_murid AS id, nama_murid AS nama, email AS email, password, "
                + "'MURID' AS role, username FROM Murid WHERE email = ? OR username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, email);
            stmt.setString(3, email);
            stmt.setString(4, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String dbPassword = rs.getString("password");

                    if (password.equals(dbPassword)) {
                        // Login berhasil — simpan ke session
                        HttpSession session = req.getSession(true);
                        session.setAttribute("userId", rs.getInt("id"));
                        session.setAttribute("userRole", rs.getString("role"));
                        session.setAttribute("userName", rs.getString("nama"));

                        // PRG pattern: redirect setelah POST
                        resp.sendRedirect(req.getContextPath() + "/dashboard");
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Terjadi kesalahan sistem. Silakan coba lagi.");
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            return;
        }

        // Login gagal — email/password salah
        req.setAttribute("error", "Email atau password salah.");
        req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
    }

    /**
     * POST /auth/register — proses registrasi guru atau murid baru.
     * Jika berhasil: redirect ke /auth/login?sukses=1 (PRG pattern).
     * Jika gagal: set error attribute, forward ke register.jsp.
     */
    private void processRegister(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String namaLengkap = req.getParameter("namaLengkap");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String konfirmasi = req.getParameter("konfirmasi");
        String role = req.getParameter("role");

        // Validasi field kosong
        if (namaLengkap == null || namaLengkap.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || password == null || password.trim().isEmpty()
                || konfirmasi == null || konfirmasi.trim().isEmpty()
                || role == null || role.trim().isEmpty()) {
            req.setAttribute("error", "Semua field wajib diisi.");
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            return;
        }

        namaLengkap = namaLengkap.trim();
        email = email.trim();
        password = password.trim();
        konfirmasi = konfirmasi.trim();
        role = role.trim();

        // Validasi konfirmasi password
        if (!password.equals(konfirmasi)) {
            req.setAttribute("error", "Password dan konfirmasi password tidak sama.");
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            return;
        }

        // Cek email sudah terdaftar (UNION Guru + Murid)
        String checkSql = "SELECT email_guru AS email FROM Guru WHERE email_guru = ? "
                + "UNION SELECT email FROM Murid WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, email);
            checkStmt.setString(2, email);

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    req.setAttribute("error", "Email sudah terdaftar.");
                    req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
                    return;
                }
            }

            // Insert user baru sesuai role
            boolean success;
            if ("Guru".equalsIgnoreCase(role)) {
                Guru guru = new Guru(namaLengkap, email, password, namaLengkap,
                        null, null, null);
                guru.setActive(false); // Default nonaktif saat registrasi
                success = guruDAO.insert(guru);
            } else if ("Murid".equalsIgnoreCase(role)) {
                Murid murid = new Murid(namaLengkap, email, password, namaLengkap,
                        null, null, null, 0, null);
                success = muridDAO.insert(murid);
            } else {
                req.setAttribute("error", "Role harus 'Guru' atau 'Murid'.");
                req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
                return;
            }

            if (success) {
                // PRG pattern: redirect setelah POST berhasil
                resp.sendRedirect(req.getContextPath() + "/auth/login?sukses=1");
            } else {
                req.setAttribute("error", "Gagal menyimpan data. Silakan coba lagi.");
                req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            }

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Terjadi kesalahan sistem: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
        }
    }
}
