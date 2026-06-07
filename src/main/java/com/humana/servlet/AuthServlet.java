package com.humana.servlet;

import com.humana.dao.*;
import com.humana.model.Guru;
import com.humana.model.Murid;
import com.humana.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Servlet untuk autentikasi dan registrasi pengguna.
 * URL Pattern: /auth/*
 *
 * <p>Adaptasi dari: authController.js + registerController.js</p>
 * <p>Perbedaan dari mobile:
 * <ul>
 *   <li>Menyimpan data ke HttpSession setelah login (web stateful)</li>
 *   <li>loginGoogle di-skip (tidak relevan untuk web monolitik)</li>
 * </ul>
 * </p>
 */
public class AuthServlet extends HttpServlet {

    private final GuruDAO guruDAO = new GuruDAOImpl();
    private final MuridDAO muridDAO = new MuridDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/";

        if ("/check-email".equals(pathInfo)) {
            checkEmail(req, resp);
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
            case "/login":
                login(req, resp);
                break;
            case "/register":
                register(req, resp);
                break;
            case "/logout":
                logout(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Login — mencari user di tabel Guru dan Murid via UNION query.
     * Adaptasi dari authController.login().
     * Setelah berhasil, menyimpan userId, userRole, userName ke HttpSession.
     */
    private void login(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String email = req.getParameter("email");       // bisa email atau username
        String password = req.getParameter("password");

        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"Email dan password wajib diisi.\"}");
            return;
        }

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

                        out.print("{\"success\":true,\"message\":\"Login berhasil.\","
                                + "\"userId\":" + rs.getInt("id") + ","
                                + "\"userRole\":\"" + rs.getString("role") + "\","
                                + "\"userName\":\"" + escapeJson(rs.getString("nama")) + "\"}");
                    } else {
                        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        out.print("{\"success\":false,\"message\":\"Password salah!\"}");
                    }
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"success\":false,\"message\":\"Akun tidak terdaftar!\"}");
                }
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    /**
     * Register — membuat akun guru atau murid baru.
     * Adaptasi dari registerController.register().
     */
    private void register(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String namaLengkap = req.getParameter("namaLengkap");
        String role = req.getParameter("role");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        if (namaLengkap == null || role == null || email == null || password == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"Semua field wajib diisi.\"}");
            return;
        }

        // Cek email sudah terdaftar
        String checkSql = "SELECT email_guru AS email FROM Guru WHERE email_guru = ? "
                + "UNION SELECT email FROM Murid WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, email);
            checkStmt.setString(2, email);

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"success\":false,\"message\":\"Email sudah terdaftar!\"}");
                    return;
                }
            }

            // Insert user baru
            if ("Guru".equalsIgnoreCase(role)) {
                Guru guru = new Guru(namaLengkap, email, password, namaLengkap,
                        null, null, null);
                guru.setActive(false); // Default nonaktif saat registrasi
                guruDAO.insert(guru);

                resp.setStatus(HttpServletResponse.SC_CREATED);
                out.print("{\"success\":true,\"message\":\"Registrasi berhasil.\","
                        + "\"userId\":" + guru.getId() + ",\"role\":\"GURU\"}");

            } else if ("Murid".equalsIgnoreCase(role)) {
                Murid murid = new Murid(namaLengkap, email, password, namaLengkap,
                        null, null, null, 0, null);
                muridDAO.insert(murid);

                resp.setStatus(HttpServletResponse.SC_CREATED);
                out.print("{\"success\":true,\"message\":\"Registrasi berhasil.\","
                        + "\"userId\":" + murid.getId() + ",\"role\":\"MURID\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\":false,\"message\":\"Role harus 'Guru' atau 'Murid'.\"}");
            }

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"message\":\"Gagal menyimpan data: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    /**
     * Check Email — cek apakah email sudah terdaftar di sistem.
     * Adaptasi dari authController.checkEmail().
     */
    private void checkEmail(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String email = req.getParameter("email");
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        if (email == null || email.isEmpty()) {
            out.print("{\"exists\":false}");
            return;
        }

        String sql = "SELECT email_guru AS email FROM Guru WHERE email_guru = ? "
                + "UNION SELECT email FROM Murid WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, email);

            try (ResultSet rs = stmt.executeQuery()) {
                out.print("{\"exists\":" + rs.next() + "}");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"exists\":false,\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    /**
     * Logout — invalidate session.
     */
    private void logout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().print("{\"success\":true,\"message\":\"Logout berhasil.\"}");
    }

    /** Escape karakter khusus untuk JSON string. */
    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r");
    }
}
