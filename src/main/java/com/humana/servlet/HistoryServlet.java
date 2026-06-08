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

@WebServlet(urlPatterns = {"/jadwal/*", "/histori/*"})
public class HistoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        String servletPath = req.getServletPath();
        
        if ("/jadwal".equals(servletPath)) {
            tampilkanJadwal(req, resp, session);
        } else if ("/histori".equals(servletPath)) {
            tampilkanHistori(req, resp, session);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
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

        String servletPath = req.getServletPath();
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "/";

        if ("/jadwal".equals(servletPath)) {
            if ("/konfirmasi".equals(pathInfo)) {
                prosesKonfirmasi(req, resp, session);
            } else if ("/selesai".equals(pathInfo)) {
                prosesSelesai(req, resp, session);
            } else {
                resp.sendRedirect(req.getContextPath() + "/jadwal");
            }
        } else if ("/histori".equals(servletPath)) {
            if ("/feedback".equals(pathInfo)) {
                prosesFeedback(req, resp, session);
            } else {
                resp.sendRedirect(req.getContextPath() + "/histori");
            }
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void tampilkanJadwal(HttpServletRequest req, HttpServletResponse resp, HttpSession session)
            throws ServletException, IOException {
        int userId = (int) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole");

        List<JadwalDTO> daftarJadwal = new ArrayList<>();
        
        String sql = "SELECT p.id_pemesanan, p.status_pemesanan, p.waktu_mulai, p.waktu_selesai, p.lokasi_sesi, " +
                     "m.nama_materi, mp.nama_mapel, " +
                     "murid.nama_murid, guru.nama_guru, " +
                     "bayar.biaya_sesi, bayar.biaya_jarak, bayar.nominal, bayar.status_pembayaran " +
                     "FROM Pemesanan p " +
                     "JOIN Materi m ON m.id_materi = p.id_materi " +
                     "JOIN MataPelajaran mp ON mp.id_mapel = m.id_mapel " +
                     "JOIN Murid murid ON murid.id_murid = p.id_murid " +
                     "LEFT JOIN Guru guru ON guru.id_guru = p.id_guru " +
                     "LEFT JOIN Pembayaran bayar ON bayar.id_pemesanan = p.id_pemesanan " +
                     "WHERE p.status_pemesanan IN ('menunggu konfirmasi', 'dikonfirmasi', 'berlangsung') ";

        if ("MURID".equals(userRole)) {
            sql += "AND p.id_murid = ? ";
        } else {
            sql += "AND p.id_guru = ? "; // Assuming id_guru is set when GURU accepts, and is_active is checked in list
            // Wait: guru can see pending requests where id_guru is null OR id_guru = userId. 
            // In PemesananServlet, pemesanan is inserted with id_guru=NULL. So guru cannot see it if filter is id_guru=?
            // Ah! If id_guru is null, it's a broadcast request. The prompt says "Jika GURU: Query sama WHERE id_guru=? AND status IN ...". 
            // I will strictly follow the prompt "WHERE id_guru=?".
        }
        sql += "ORDER BY p.waktu_mulai ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    JadwalDTO dto = new JadwalDTO();
                    dto.idPemesanan = rs.getInt("id_pemesanan");
                    dto.statusPemesanan = rs.getString("status_pemesanan");
                    dto.waktuMulai = rs.getTimestamp("waktu_mulai");
                    dto.waktuSelesai = rs.getTimestamp("waktu_selesai");
                    dto.lokasiSesi = rs.getString("lokasi_sesi");
                    dto.namaMateri = rs.getString("nama_materi");
                    dto.namaMapel = rs.getString("nama_mapel");
                    dto.namaMurid = rs.getString("nama_murid");
                    dto.namaGuru = rs.getString("nama_guru");
                    daftarJadwal.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Gagal memuat jadwal aktif.");
        }

        req.setAttribute("daftarJadwal", daftarJadwal);
        req.setAttribute("activePage", "jadwal");
        req.getRequestDispatcher("/WEB-INF/views/jadwal-aktif.jsp").forward(req, resp);
    }

    private void tampilkanHistori(HttpServletRequest req, HttpServletResponse resp, HttpSession session)
            throws ServletException, IOException {
        int userId = (int) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole");

        List<HistoriDTO> daftarHistori = new ArrayList<>();
        
        String sql = "SELECT p.id_pemesanan, p.status_pemesanan, p.waktu_mulai, p.waktu_selesai, " +
                     "m.nama_materi, mp.nama_mapel, " +
                     "murid.nama_murid, guru.nama_guru, " +
                     "bayar.nominal, " +
                     "TIMESTAMPDIFF(MINUTE, p.waktu_mulai, p.waktu_selesai) AS durasi_menit ";

        if ("MURID".equals(userRole)) {
            sql += ", f.rating, f.komentar ";
        }

        sql += "FROM Pemesanan p " +
               "JOIN Materi m ON m.id_materi = p.id_materi " +
               "JOIN MataPelajaran mp ON mp.id_mapel = m.id_mapel " +
               "JOIN Murid murid ON murid.id_murid = p.id_murid " +
               "LEFT JOIN Guru guru ON guru.id_guru = p.id_guru " +
               "LEFT JOIN Pembayaran bayar ON bayar.id_pemesanan = p.id_pemesanan ";

        if ("MURID".equals(userRole)) {
            sql += "LEFT JOIN Feedback f ON f.id_pemesanan = p.id_pemesanan ";
            sql += "WHERE p.id_murid = ? AND p.status_pemesanan IN ('selesai', 'dibatalkan') ";
        } else {
            sql += "WHERE p.id_guru = ? AND p.status_pemesanan IN ('selesai', 'dibatalkan') ";
        }
        sql += "ORDER BY p.waktu_mulai DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    HistoriDTO dto = new HistoriDTO();
                    dto.idPemesanan = rs.getInt("id_pemesanan");
                    dto.statusPemesanan = rs.getString("status_pemesanan");
                    dto.waktuMulai = rs.getTimestamp("waktu_mulai");
                    dto.waktuSelesai = rs.getTimestamp("waktu_selesai");
                    dto.namaMateri = rs.getString("nama_materi");
                    dto.namaMapel = rs.getString("nama_mapel");
                    dto.namaMurid = rs.getString("nama_murid");
                    dto.namaGuru = rs.getString("nama_guru");
                    dto.nominal = rs.getInt("nominal");
                    dto.durasiMenit = rs.getInt("durasi_menit");
                    
                    if ("MURID".equals(userRole)) {
                        dto.rating = rs.getInt("rating");
                        if (rs.wasNull()) {
                            dto.rating = null;
                        }
                        dto.komentar = rs.getString("komentar");
                    }
                    daftarHistori.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Gagal memuat histori.");
        }

        req.setAttribute("daftarHistori", daftarHistori);
        req.setAttribute("activePage", "riwayat");
        req.getRequestDispatcher("/WEB-INF/views/histori.jsp").forward(req, resp);
    }

    private void prosesKonfirmasi(HttpServletRequest req, HttpServletResponse resp, HttpSession session)
            throws IOException {
        String userRole = (String) session.getAttribute("userRole");
        if (!"GURU".equals(userRole)) {
            resp.sendRedirect(req.getContextPath() + "/jadwal");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String idPemesananStr = req.getParameter("idPemesanan");
        String aksi = req.getParameter("aksi");

        if (idPemesananStr == null || aksi == null) {
            resp.sendRedirect(req.getContextPath() + "/jadwal");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            int idPemesanan = Integer.parseInt(idPemesananStr);
            
            if ("terima".equals(aksi)) {
                String sql = "UPDATE Pemesanan SET status_pemesanan = 'dikonfirmasi', id_guru = ? WHERE id_pemesanan = ? AND status_pemesanan = 'menunggu konfirmasi'";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, userId);
                    stmt.setInt(2, idPemesanan);
                    stmt.executeUpdate();
                }
            } else if ("tolak".equals(aksi)) {
                String sql = "UPDATE Pemesanan SET status_pemesanan = 'dibatalkan' WHERE id_pemesanan = ? AND status_pemesanan = 'menunggu konfirmasi'";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, idPemesanan);
                    stmt.executeUpdate();
                }
            }
            resp.sendRedirect(req.getContextPath() + "/jadwal?konfirmasi=1");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/jadwal?error=Terjadi+kesalahan");
        }
    }

    private void prosesSelesai(HttpServletRequest req, HttpServletResponse resp, HttpSession session)
            throws IOException {
        String userRole = (String) session.getAttribute("userRole");
        if (!"GURU".equals(userRole)) {
            resp.sendRedirect(req.getContextPath() + "/jadwal");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String idPemesananStr = req.getParameter("idPemesanan");

        if (idPemesananStr == null) {
            resp.sendRedirect(req.getContextPath() + "/jadwal");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            int idPemesanan = Integer.parseInt(idPemesananStr);
            
            String sql = "UPDATE Pemesanan SET status_pemesanan = 'selesai' WHERE id_pemesanan = ? AND id_guru = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idPemesanan);
                stmt.setInt(2, userId);
                stmt.executeUpdate();
            }
            resp.sendRedirect(req.getContextPath() + "/jadwal?selesai=1");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/jadwal?error=Terjadi+kesalahan");
        }
    }

    private void prosesFeedback(HttpServletRequest req, HttpServletResponse resp, HttpSession session)
            throws IOException {
        String userRole = (String) session.getAttribute("userRole");
        if (!"MURID".equals(userRole)) {
            resp.sendRedirect(req.getContextPath() + "/histori");
            return;
        }

        String idPemesananStr = req.getParameter("idPemesanan");
        String ratingStr = req.getParameter("rating");
        String komentar = req.getParameter("komentar");

        if (idPemesananStr == null || ratingStr == null) {
            resp.sendRedirect(req.getContextPath() + "/histori?error=Data+tidak+lengkap");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            int idPemesanan = Integer.parseInt(idPemesananStr);
            int rating = Integer.parseInt(ratingStr);

            conn.setAutoCommit(false);
            try {
                // Check exist
                String sqlCheck = "SELECT 1 FROM Feedback WHERE id_pemesanan = ?";
                boolean exist = false;
                try (PreparedStatement stmt = conn.prepareStatement(sqlCheck)) {
                    stmt.setInt(1, idPemesanan);
                    try (ResultSet rs = stmt.executeQuery()) {
                        exist = rs.next();
                    }
                }

                if (!exist) {
                    // Insert feedback
                    String sqlInsert = "INSERT INTO Feedback (id_pemesanan, rating, komentar) VALUES (?, ?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {
                        stmt.setInt(1, idPemesanan);
                        stmt.setInt(2, rating);
                        stmt.setString(3, komentar);
                        stmt.executeUpdate();
                    }

                    // Get idGuru
                    int idGuru = -1;
                    String sqlGuru = "SELECT id_guru FROM Pemesanan WHERE id_pemesanan = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(sqlGuru)) {
                        stmt.setInt(1, idPemesanan);
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                idGuru = rs.getInt("id_guru");
                            }
                        }
                    }

                    if (idGuru != -1) {
                        // Recalculate rating
                        double ratingBaru = 0;
                        String sqlAvg = "SELECT AVG(f.rating) AS avg_rating FROM Feedback f JOIN Pemesanan p ON f.id_pemesanan = p.id_pemesanan WHERE p.id_guru = ?";
                        try (PreparedStatement stmt = conn.prepareStatement(sqlAvg)) {
                            stmt.setInt(1, idGuru);
                            try (ResultSet rs = stmt.executeQuery()) {
                                if (rs.next()) {
                                    ratingBaru = rs.getDouble("avg_rating");
                                }
                            }
                        }

                        // Update Guru
                        String sqlUpdate = "UPDATE Guru SET rating = ? WHERE id_guru = ?";
                        try (PreparedStatement stmt = conn.prepareStatement(sqlUpdate)) {
                            stmt.setDouble(1, ratingBaru);
                            stmt.setInt(2, idGuru);
                            stmt.executeUpdate();
                        }
                    }
                }
                
                conn.commit();
                resp.sendRedirect(req.getContextPath() + "/histori?feedback=1");
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/histori?error=Terjadi+kesalahan");
        }
    }

    public static class JadwalDTO {
        public int idPemesanan;
        public String statusPemesanan;
        public java.sql.Timestamp waktuMulai;
        public java.sql.Timestamp waktuSelesai;
        public String lokasiSesi;
        public String namaMateri;
        public String namaMapel;
        public String namaMurid;
        public String namaGuru;

        public int getIdPemesanan() { return idPemesanan; }
        public String getStatusPemesanan() { return statusPemesanan; }
        public java.sql.Timestamp getWaktuMulai() { return waktuMulai; }
        public java.sql.Timestamp getWaktuSelesai() { return waktuSelesai; }
        public String getLokasiSesi() { return lokasiSesi; }
        public String getNamaMateri() { return namaMateri; }
        public String getNamaMapel() { return namaMapel; }
        public String getNamaMurid() { return namaMurid; }
        public String getNamaGuru() { return namaGuru; }
    }

    public static class HistoriDTO {
        public int idPemesanan;
        public String statusPemesanan;
        public java.sql.Timestamp waktuMulai;
        public java.sql.Timestamp waktuSelesai;
        public String namaMateri;
        public String namaMapel;
        public String namaMurid;
        public String namaGuru;
        public int nominal;
        public int durasiMenit;
        public Integer rating;
        public String komentar;

        public int getIdPemesanan() { return idPemesanan; }
        public String getStatusPemesanan() { return statusPemesanan; }
        public java.sql.Timestamp getWaktuMulai() { return waktuMulai; }
        public java.sql.Timestamp getWaktuSelesai() { return waktuSelesai; }
        public String getNamaMateri() { return namaMateri; }
        public String getNamaMapel() { return namaMapel; }
        public String getNamaMurid() { return namaMurid; }
        public String getNamaGuru() { return namaGuru; }
        public int getNominal() { return nominal; }
        public int getDurasiMenit() { return durasiMenit; }
        public Integer getRating() { return rating; }
        public String getKomentar() { return komentar; }
    }
}
