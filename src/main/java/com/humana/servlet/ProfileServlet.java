package com.humana.servlet;

import com.humana.dao.GuruDAO;
import com.humana.dao.MateriGuruDAO;
import com.humana.dao.MuridDAO;
import com.humana.dao.PortfolioDAO;
import com.humana.model.Guru;
import com.humana.model.Murid;
import com.humana.model.Portfolio;
import com.humana.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servlet untuk manajemen profil pengguna (Guru & Murid).
 * URL Pattern: /profile/* dan /profil/*
 */
public class ProfileServlet extends HttpServlet {

    private final GuruDAO guruDAO = new GuruDAO();
    private final MuridDAO muridDAO = new MuridDAO();
    private final PortfolioDAO portfolioDAO = new PortfolioDAO();
    private final MateriGuruDAO materiGuruDAO = new MateriGuruDAO();

    private String profilUrl(HttpServletRequest req) {
        return req.getContextPath() + "/profil";
    }

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
            showProfile(req, resp, session);
        } else {
            resp.sendRedirect(profilUrl(req));
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
            case "/portfolio/tambah":
                tambahPortfolio(req, resp, session);
                break;
            case "/portfolio/hapus":
                hapusPortfolio(req, resp, session);
                break;
            case "/materi/simpan":
                simpanMateriGuru(req, resp, session);
                break;
            default:
                resp.sendRedirect(profilUrl(req));
        }
    }

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
                req.setAttribute("daftarPortfolio", portfolioDAO.findByGuruId(userId));
                req.setAttribute("materiGuruList", materiGuruDAO.findMateriDetailByGuruId(userId));
                req.setAttribute("semuaMateri", loadSemuaMateriUntukPilih());

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

    private List<MateriPilihDTO> loadSemuaMateriUntukPilih() {
        List<MateriPilihDTO> list = new ArrayList<>();
        String sql = "SELECT m.id_materi, m.nama_materi, m.kelas, mp.nama_mapel, mp.jenjang "
                + "FROM Materi m JOIN MataPelajaran mp ON m.id_mapel = mp.id_mapel ORDER BY mp.jenjang, m.kelas, m.nama_materi";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                MateriPilihDTO d = new MateriPilihDTO();
                d.idMateri = rs.getInt("id_materi");
                d.namaMateri = rs.getString("nama_materi");
                d.kelas = rs.getInt("kelas");
                d.namaMapel = rs.getString("nama_mapel");
                d.jenjang = rs.getString("jenjang");
                list.add(d);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

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

            if (nama != null && !nama.trim().isEmpty()) {
                session.setAttribute("userName", nama.trim());
            }

            resp.sendRedirect(profilUrl(req) + "?sukses=1");

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(profilUrl(req) + "?error=Gagal+memperbarui+profil");
        }
    }

    private void updateAcademic(HttpServletRequest req, HttpServletResponse resp, HttpSession session)
            throws IOException {
        String userRole = (String) session.getAttribute("userRole");
        if (!"MURID".equals(userRole)) {
            resp.sendRedirect(profilUrl(req));
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String kelasStr = req.getParameter("kelas");
        String jenjang = req.getParameter("jenjang");

        try {
            int kelas = 0;
            if (kelasStr != null && !kelasStr.trim().isEmpty()) {
                kelas = Integer.parseInt(kelasStr.trim());
            }

            if (jenjang == null || jenjang.isBlank() || kelas <= 0) {
                resp.sendRedirect(profilUrl(req) + "?error=Jenjang+dan+kelas+wajib+dipilih");
                return;
            }

            if (!isKelasValid(jenjang, kelas)) {
                resp.sendRedirect(profilUrl(req) + "?error=Kelas+tidak+sesuai+dengan+jenjang");
                return;
            }

            String sql = "UPDATE Murid SET kelas = ?, jurusan = ? WHERE id_murid = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, kelas);
                stmt.setString(2, jenjang.trim());
                stmt.setInt(3, userId);
                stmt.executeUpdate();
            }

            resp.sendRedirect(profilUrl(req) + "?sukses=1");

        } catch (NumberFormatException e) {
            resp.sendRedirect(profilUrl(req) + "?error=Kelas+harus+berupa+angka");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(profilUrl(req) + "?error=Gagal+memperbarui+profil+akademik");
        }
    }

    private boolean isKelasValid(String jenjang, int kelas) {
        return switch (jenjang) {
            case "SD" -> kelas >= 1 && kelas <= 6;
            case "SMP" -> kelas >= 7 && kelas <= 9;
            case "SMA", "SMK" -> kelas >= 10 && kelas <= 12;
            default -> false;
        };
    }

    private void updateAvailability(HttpServletRequest req, HttpServletResponse resp, HttpSession session)
            throws IOException {
        String userRole = (String) session.getAttribute("userRole");
        if (!"GURU".equals(userRole)) {
            resp.sendRedirect(profilUrl(req));
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
            resp.sendRedirect(profilUrl(req) + "?sukses=1");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(profilUrl(req) + "?error=Gagal+memperbarui+ketersediaan");
        }
    }

    private void tambahPortfolio(HttpServletRequest req, HttpServletResponse resp, HttpSession session)
            throws IOException {
        if (!"GURU".equals(session.getAttribute("userRole"))) {
            resp.sendRedirect(profilUrl(req));
            return;
        }
        int idGuru = (int) session.getAttribute("userId");

        String judul = req.getParameter("judul");
        String deskripsi = req.getParameter("deskripsi");
        String tipe = req.getParameter("tipePortfolio");
        String bukti = req.getParameter("bukti");
        String tglMulai = req.getParameter("tanggalMulai");
        String tglSelesai = req.getParameter("tanggalSelesai");

        if (judul == null || judul.isBlank() || deskripsi == null || deskripsi.isBlank()
                || tipe == null || tipe.isBlank() || bukti == null || bukti.isBlank()) {
            resp.sendRedirect(profilUrl(req) + "?error=Portfolio:+semua+field+wajib+diisi&tab=portfolio");
            return;
        }

        Portfolio p = new Portfolio();
        p.setIdGuru(idGuru);
        p.setJudul(judul.trim());
        p.setDeskripsi(deskripsi.trim());
        p.setTipePortfolio(tipe.trim());
        p.setBukti(bukti.trim());
        if (tglMulai != null && !tglMulai.isBlank()) {
            p.setTanggalMulai(Date.valueOf(tglMulai));
        }
        if (tglSelesai != null && !tglSelesai.isBlank()) {
            p.setTanggalSelesai(Date.valueOf(tglSelesai));
        }

        if (portfolioDAO.insert(p)) {
            resp.sendRedirect(profilUrl(req) + "?sukses=1&tab=portfolio");
        } else {
            resp.sendRedirect(profilUrl(req) + "?error=Gagal+menyimpan+portfolio&tab=portfolio");
        }
    }

    private void hapusPortfolio(HttpServletRequest req, HttpServletResponse resp, HttpSession session)
            throws IOException {
        if (!"GURU".equals(session.getAttribute("userRole"))) {
            resp.sendRedirect(profilUrl(req));
            return;
        }
        int idGuru = (int) session.getAttribute("userId");
        String idStr = req.getParameter("idPortfolio");
        if (idStr == null) {
            resp.sendRedirect(profilUrl(req) + "?tab=portfolio");
            return;
        }
        portfolioDAO.delete(Integer.parseInt(idStr), idGuru);
        resp.sendRedirect(profilUrl(req) + "?sukses=1&tab=portfolio");
    }

    private void simpanMateriGuru(HttpServletRequest req, HttpServletResponse resp, HttpSession session)
            throws IOException {
        if (!"GURU".equals(session.getAttribute("userRole"))) {
            resp.sendRedirect(profilUrl(req));
            return;
        }
        int idGuru = (int) session.getAttribute("userId");
        String[] ids = req.getParameterValues("idMateri");
        List<Integer> idList = ids == null ? List.of() :
                Arrays.stream(ids).map(Integer::parseInt).collect(Collectors.toList());

        try {
            materiGuruDAO.syncMateriGuru(idGuru, idList);
            resp.sendRedirect(profilUrl(req) + "?sukses=1&tab=materi");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(profilUrl(req) + "?error=Gagal+menyimpan+materi&tab=materi");
        }
    }

    public static class MateriPilihDTO {
        public int idMateri;
        public String namaMateri;
        public int kelas;
        public String namaMapel;
        public String jenjang;

        public int getIdMateri() { return idMateri; }
        public String getNamaMateri() { return namaMateri; }
        public int getKelas() { return kelas; }
        public String getNamaMapel() { return namaMapel; }
        public String getJenjang() { return jenjang; }
    }
}
