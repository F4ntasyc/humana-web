package com.humana.servlet;

import com.humana.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet untuk materi, mata pelajaran, dan relasi materi-guru.
 * URL Pattern: /materi/*
 *
 * <p>Adaptasi dari: MateriController.js + materiGuruController.js + pemesananController (dropdown)</p>
 * <p>Perbaikan: Semua endpoint terkait materi dikonsolidasi di sini.
 * Dropdown materi/mapel dipindahkan dari PemesananServlet.</p>
 */
public class MateriServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/";

        switch (pathInfo) {
            case "/dropdown":
                getMateriDropdown(req, resp);
                break;
            case "/mapel":
                getMapelByJenjang(req, resp);
                break;
            case "/by-mapel":
                getMateriBySubject(req, resp);
                break;
            case "/all":
                getAllMapel(req, resp);
                break;
            default:
                // /guru/{id_guru}
                if (pathInfo.startsWith("/guru/")) {
                    getMateriGuru(req, resp, pathInfo.substring(6));
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/";

        switch (pathInfo) {
            case "/guru/simpan":
                simpanMateriGuru(req, resp);
                break;
            case "/guru/hapus":
                hapusMateriGuru(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    // ======== Materi & Mapel ========

    /**
     * Dropdown materi dengan JOIN MataPelajaran, filter id_mapel/nama_mapel/kelas.
     * Dipindahkan dari PemesananServlet. Sumber asli: pemesananController.getMateriDropdown().
     */
    private void getMateriDropdown(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String idMapel = req.getParameter("id_mapel");
        String mapel = req.getParameter("mapel");
        String kelas = req.getParameter("kelas");

        try {
            StringBuilder sql = new StringBuilder(
                    "SELECT m.id_materi, m.nama_materi, m.kelas, m.jurusan, m.id_mapel, "
                    + "mp.nama_mapel, mp.jenjang FROM Materi m "
                    + "LEFT JOIN MataPelajaran mp ON m.id_mapel = mp.id_mapel WHERE 1=1");

            List<Object> params = new ArrayList<>();

            if (idMapel != null && !idMapel.isEmpty()) {
                sql.append(" AND m.id_mapel = ?");
                params.add(Integer.parseInt(idMapel));
            } else if (mapel != null && !mapel.isEmpty()) {
                sql.append(" AND mp.nama_mapel = ?");
                params.add(mapel);
            }
            if (kelas != null && !kelas.isEmpty()) {
                sql.append(" AND m.kelas = ?");
                params.add(Integer.parseInt(kelas));
            }

            StringBuilder dataJson = new StringBuilder("[");
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

                for (int i = 0; i < params.size(); i++) {
                    stmt.setObject(i + 1, params.get(i));
                }

                try (ResultSet rs = stmt.executeQuery()) {
                    boolean first = true;
                    while (rs.next()) {
                        if (!first) dataJson.append(",");
                        dataJson.append("{\"id_materi\":").append(rs.getInt("id_materi"))
                                .append(",\"nama_materi\":\"").append(escapeJson(rs.getString("nama_materi"))).append("\"")
                                .append(",\"kelas\":").append(rs.getInt("kelas"))
                                .append(",\"jurusan\":\"").append(escapeJson(rs.getString("jurusan"))).append("\"")
                                .append(",\"id_mapel\":").append(rs.getInt("id_mapel"))
                                .append(",\"nama_mapel\":\"").append(escapeJson(rs.getString("nama_mapel"))).append("\"")
                                .append(",\"jenjang\":\"").append(escapeJson(rs.getString("jenjang"))).append("\"")
                                .append("}");
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
     * Dropdown mata pelajaran, opsional filter jenjang.
     * Dipindahkan dari PemesananServlet. Sumber asli: pemesananController.getMapelByJenjang().
     */
    private void getMapelByJenjang(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String jenjang = req.getParameter("jenjang");

        try {
            String sql = (jenjang != null && !jenjang.isEmpty())
                    ? "SELECT id_mapel, nama_mapel, jenjang FROM MataPelajaran WHERE jenjang = ?"
                    : "SELECT id_mapel, nama_mapel, jenjang FROM MataPelajaran";

            StringBuilder dataJson = new StringBuilder("[");
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                if (jenjang != null && !jenjang.isEmpty()) {
                    stmt.setString(1, jenjang);
                }

                try (ResultSet rs = stmt.executeQuery()) {
                    boolean first = true;
                    while (rs.next()) {
                        if (!first) dataJson.append(",");
                        dataJson.append("{\"id\":").append(rs.getInt("id_mapel"))
                                .append(",\"namaMapel\":\"").append(escapeJson(rs.getString("nama_mapel"))).append("\"")
                                .append(",\"jenjang\":\"").append(escapeJson(rs.getString("jenjang"))).append("\"")
                                .append("}");
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
     * Materi per mata pelajaran (by id_mapel).
     * Adaptasi dari MateriController.getMateriBySubject().
     */
    private void getMateriBySubject(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String idMapel = req.getParameter("id_mapel");
        if (idMapel == null || idMapel.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"Parameter id_mapel wajib diisi.\"}");
            return;
        }

        try {
            String sql = "SELECT id_materi AS id, nama_materi AS namaMateri, kelas, jurusan, "
                    + "deskripsi AS deskripsiMateri FROM Materi WHERE id_mapel = ? ORDER BY id_materi ASC";

            StringBuilder dataJson = new StringBuilder("[");
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, Integer.parseInt(idMapel));
                try (ResultSet rs = stmt.executeQuery()) {
                    boolean first = true;
                    while (rs.next()) {
                        if (!first) dataJson.append(",");
                        dataJson.append("{\"id\":").append(rs.getInt("id"))
                                .append(",\"namaMateri\":\"").append(escapeJson(rs.getString("namaMateri"))).append("\"")
                                .append(",\"kelas\":").append(rs.getInt("kelas"))
                                .append(",\"jurusan\":\"").append(escapeJson(rs.getString("jurusan"))).append("\"")
                                .append(",\"deskripsiMateri\":\"").append(escapeJson(rs.getString("deskripsiMateri"))).append("\"")
                                .append("}");
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
     * Semua mata pelajaran.
     * Adaptasi dari MateriController.getAllMapel().
     */
    private void getAllMapel(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            String sql = "SELECT id_mapel, nama_mapel FROM MataPelajaran ORDER BY id_mapel ASC";

            StringBuilder dataJson = new StringBuilder("[");
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                boolean first = true;
                while (rs.next()) {
                    if (!first) dataJson.append(",");
                    dataJson.append("{\"id_mapel\":").append(rs.getInt("id_mapel"))
                            .append(",\"nama_mapel\":\"").append(escapeJson(rs.getString("nama_mapel"))).append("\"")
                            .append("}");
                    first = false;
                }
            }
            dataJson.append("]");

            out.print("{\"success\":true,\"data\":" + dataJson + "}");

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    // ======== Materi Guru ========

    /**
     * Materi yang diampu guru (dengan JOIN ke tabel Materi & MataPelajaran).
     * Adaptasi dari materiGuruController.getMateriGuru().
     */
    private void getMateriGuru(HttpServletRequest req, HttpServletResponse resp, String idGuruStr)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            int idGuru = Integer.parseInt(idGuruStr);

            String sql = "SELECT mg.id_materi, m.nama_materi, m.kelas, m.id_mapel, "
                    + "mp.nama_mapel, mp.jenjang FROM MateriGuru mg "
                    + "JOIN Materi m ON mg.id_materi = m.id_materi "
                    + "JOIN MataPelajaran mp ON m.id_mapel = mp.id_mapel "
                    + "WHERE mg.id_guru = ?";

            StringBuilder dataJson = new StringBuilder("[");
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, idGuru);
                try (ResultSet rs = stmt.executeQuery()) {
                    boolean first = true;
                    while (rs.next()) {
                        if (!first) dataJson.append(",");
                        dataJson.append("{\"id_materi\":").append(rs.getInt("id_materi"))
                                .append(",\"nama_materi\":\"").append(escapeJson(rs.getString("nama_materi"))).append("\"")
                                .append(",\"kelas\":").append(rs.getInt("kelas"))
                                .append(",\"id_mapel\":").append(rs.getInt("id_mapel"))
                                .append(",\"nama_mapel\":\"").append(escapeJson(rs.getString("nama_mapel"))).append("\"")
                                .append(",\"jenjang\":\"").append(escapeJson(rs.getString("jenjang"))).append("\"")
                                .append("}");
                        first = false;
                    }
                }
            }
            dataJson.append("]");

            out.print("{\"success\":true,\"data\":" + dataJson + "}");

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"id_guru tidak valid.\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    /**
     * Sinkronkan materi guru: hapus yang tidak lagi dipilih, insert yang baru.
     * Adaptasi dari materiGuruController.simpanMateriGuru().
     * Menggunakan transaction untuk konsistensi data.
     */
    private void simpanMateriGuru(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String idGuruStr = req.getParameter("id_guru");
        String idMateriListStr = req.getParameter("id_materi_list"); // comma-separated: "1,2,5"

        if (idGuruStr == null || idGuruStr.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"id_guru tidak valid.\"}");
            return;
        }

        try {
            int idGuru = Integer.parseInt(idGuruStr);

            // Parse comma-separated list → integer list
            List<Integer> cleanList = new ArrayList<>();
            if (idMateriListStr != null && !idMateriListStr.isEmpty()) {
                for (String s : idMateriListStr.split(",")) {
                    try {
                        int val = Integer.parseInt(s.trim());
                        if (val > 0 && !cleanList.contains(val)) {
                            cleanList.add(val);
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }

            try (Connection conn = DBConnection.getConnection()) {
                conn.setAutoCommit(false);
                try {
                    if (!cleanList.isEmpty()) {
                        // 1. Hapus materi yang tidak ada lagi di daftar
                        StringBuilder placeholders = new StringBuilder();
                        for (int i = 0; i < cleanList.size(); i++) {
                            if (i > 0) placeholders.append(",");
                            placeholders.append("?");
                        }
                        String deleteSql = "DELETE FROM MateriGuru WHERE id_guru = ? "
                                + "AND id_materi NOT IN (" + placeholders + ")";
                        try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
                            stmt.setInt(1, idGuru);
                            for (int i = 0; i < cleanList.size(); i++) {
                                stmt.setInt(i + 2, cleanList.get(i));
                            }
                            stmt.executeUpdate();
                        }

                        // 2. Insert baru (IGNORE duplikat)
                        StringBuilder valueTuples = new StringBuilder();
                        for (int i = 0; i < cleanList.size(); i++) {
                            if (i > 0) valueTuples.append(",");
                            valueTuples.append("(?, ?)");
                        }
                        String insertSql = "INSERT IGNORE INTO MateriGuru (id_guru, id_materi) VALUES "
                                + valueTuples;
                        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                            int idx = 1;
                            for (int idMateri : cleanList) {
                                stmt.setInt(idx++, idGuru);
                                stmt.setInt(idx++, idMateri);
                            }
                            stmt.executeUpdate();
                        }
                    } else {
                        // Daftar kosong → hapus semua
                        try (PreparedStatement stmt = conn.prepareStatement(
                                "DELETE FROM MateriGuru WHERE id_guru = ?")) {
                            stmt.setInt(1, idGuru);
                            stmt.executeUpdate();
                        }
                    }

                    conn.commit();
                    out.print("{\"success\":true,\"message\":\"Materi berhasil disimpan.\"}");

                } catch (Exception e) {
                    conn.rollback();
                    throw e;
                } finally {
                    conn.setAutoCommit(true);
                }
            }

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    /**
     * Hapus satu relasi materi-guru.
     * Adaptasi dari materiGuruController.hapusMateriGuru().
     */
    private void hapusMateriGuru(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String idGuruStr = req.getParameter("id_guru");
        String idMateriStr = req.getParameter("id_materi");

        if (idGuruStr == null || idMateriStr == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"id_guru dan id_materi wajib diisi.\"}");
            return;
        }

        try {
            String sql = "DELETE FROM MateriGuru WHERE id_guru = ? AND id_materi = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, Integer.parseInt(idGuruStr));
                stmt.setInt(2, Integer.parseInt(idMateriStr));
                stmt.executeUpdate();
            }

            out.print("{\"success\":true,\"message\":\"Materi berhasil dihapus.\"}");

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
