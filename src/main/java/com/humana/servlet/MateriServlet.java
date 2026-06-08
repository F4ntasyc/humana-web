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
 * Servlet untuk materi, mata pelajaran.
 * URL Pattern: /materi/*
 */
public class MateriServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || "/".equals(pathInfo)) {
            listMateri(req, resp);
        } else if ("/detail".equals(pathInfo)) {
            detailMateri(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/materi");
        }
    }

    private void listMateri(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String jenjang = req.getParameter("jenjang");
        List<MateriDTO> daftarMateri = new ArrayList<>();

        String sql = "SELECT m.id_materi, m.nama_materi, m.kelas, m.jurusan, m.deskripsi, " +
                     "mp.nama_mapel, mp.jenjang " +
                     "FROM Materi m JOIN MataPelajaran mp ON m.id_mapel = mp.id_mapel ";
        
        if (jenjang != null && !jenjang.isEmpty() && !jenjang.equals("Semua")) {
            sql += "WHERE mp.jenjang = ? ";
        }
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            if (jenjang != null && !jenjang.isEmpty() && !jenjang.equals("Semua")) {
                stmt.setString(1, jenjang);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MateriDTO dto = new MateriDTO();
                    dto.setIdMateri(rs.getInt("id_materi"));
                    dto.setNamaMateri(rs.getString("nama_materi"));
                    dto.setKelas(rs.getInt("kelas"));
                    dto.setJurusan(rs.getString("jurusan"));
                    dto.setDeskripsi(rs.getString("deskripsi"));
                    dto.setNamaMapel(rs.getString("nama_mapel"));
                    dto.setJenjang(rs.getString("jenjang"));
                    daftarMateri.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Terjadi kesalahan saat memuat materi.");
        }

        req.setAttribute("daftarMateri", daftarMateri);
        req.setAttribute("jenjangFilter", jenjang);
        req.setAttribute("activePage", "materi");
        req.getRequestDispatcher("/WEB-INF/views/materi/list.jsp").forward(req, resp);
    }

    private void detailMateri(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/materi");
            return;
        }

        MateriDTO materi = null;
        String sql = "SELECT m.id_materi, m.nama_materi, m.kelas, m.jurusan, m.deskripsi, " +
                     "mp.nama_mapel, mp.jenjang " +
                     "FROM Materi m JOIN MataPelajaran mp ON m.id_mapel = mp.id_mapel " +
                     "WHERE m.id_materi = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, Integer.parseInt(idStr));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    materi = new MateriDTO();
                    materi.setIdMateri(rs.getInt("id_materi"));
                    materi.setNamaMateri(rs.getString("nama_materi"));
                    materi.setKelas(rs.getInt("kelas"));
                    materi.setJurusan(rs.getString("jurusan"));
                    materi.setDeskripsi(rs.getString("deskripsi"));
                    materi.setNamaMapel(rs.getString("nama_mapel"));
                    materi.setJenjang(rs.getString("jenjang"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Terjadi kesalahan saat memuat detail materi.");
        }

        if (materi == null) {
            resp.sendRedirect(req.getContextPath() + "/materi");
            return;
        }

        req.setAttribute("materi", materi);
        req.setAttribute("activePage", "materi");
        req.getRequestDispatcher("/WEB-INF/views/materi/detail.jsp").forward(req, resp);
    }

    // DTO class for JSP mapping
    public static class MateriDTO {
        private int idMateri;
        private String namaMateri;
        private int kelas;
        private String jurusan;
        private String deskripsi;
        private String namaMapel;
        private String jenjang;

        public int getIdMateri() { return idMateri; }
        public void setIdMateri(int idMateri) { this.idMateri = idMateri; }
        public String getNamaMateri() { return namaMateri; }
        public void setNamaMateri(String namaMateri) { this.namaMateri = namaMateri; }
        public int getKelas() { return kelas; }
        public void setKelas(int kelas) { this.kelas = kelas; }
        public String getJurusan() { return jurusan; }
        public void setJurusan(String jurusan) { this.jurusan = jurusan; }
        public String getDeskripsi() { return deskripsi; }
        public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
        public String getNamaMapel() { return namaMapel; }
        public void setNamaMapel(String namaMapel) { this.namaMapel = namaMapel; }
        public String getJenjang() { return jenjang; }
        public void setJenjang(String jenjang) { this.jenjang = jenjang; }
    }
}
