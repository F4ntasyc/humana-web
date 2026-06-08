package com.humana.dao;

import com.humana.model.Pemesanan;
import com.humana.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO untuk entitas Pemesanan.
 * Semua operasi database menggunakan try-with-resources.
 */
public class PemesananDAO {

    private Pemesanan mapResultSetToPemesanan(ResultSet rs) throws SQLException {
        Pemesanan p = new Pemesanan();
        p.setIdPemesanan(rs.getInt("id_pemesanan"));
        p.setIdMurid(rs.getInt("id_murid"));
        p.setIdGuru(rs.getInt("id_guru"));
        p.setIdMateri(rs.getInt("id_materi"));
        p.setStatusPemesanan(rs.getString("status_pemesanan"));

        Timestamp tsMulai = rs.getTimestamp("waktu_mulai");
        if (tsMulai != null) {
            p.setWaktuMulai(tsMulai.toLocalDateTime());
        }
        Timestamp tsSelesai = rs.getTimestamp("waktu_selesai");
        if (tsSelesai != null) {
            p.setWaktuSelesai(tsSelesai.toLocalDateTime());
        }

        p.setLokasiSesi(rs.getString("lokasi_sesi"));
        p.setFotoDokumentasi(rs.getString("foto_dokumentasi"));
        return p;
    }

    public Pemesanan findById(int id) {
        String sql = "SELECT * FROM pemesanan WHERE id_pemesanan = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPemesanan(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Pemesanan> findByMuridId(int idMurid) {
        String sql = "SELECT * FROM pemesanan WHERE id_murid = ? ORDER BY waktu_mulai DESC";
        List<Pemesanan> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMurid);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPemesanan(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Pemesanan> findByGuruId(int idGuru) {
        String sql = "SELECT * FROM pemesanan WHERE id_guru = ? ORDER BY waktu_mulai DESC";
        List<Pemesanan> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idGuru);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPemesanan(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Pemesanan> findByStatus(String statusPemesanan) {
        String sql = "SELECT * FROM pemesanan WHERE status_pemesanan = ? ORDER BY waktu_mulai DESC";
        List<Pemesanan> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, statusPemesanan);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPemesanan(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Pemesanan> findAll() {
        String sql = "SELECT * FROM pemesanan ORDER BY waktu_mulai DESC";
        List<Pemesanan> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToPemesanan(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(Pemesanan p) {
        String sql = "INSERT INTO pemesanan (id_murid, id_guru, id_materi, status_pemesanan, "
                   + "waktu_mulai, waktu_selesai, lokasi_sesi, foto_dokumentasi) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, p.getIdMurid());
            stmt.setInt(2, p.getIdGuru());
            stmt.setInt(3, p.getIdMateri());
            stmt.setString(4, p.getStatusPemesanan());
            stmt.setTimestamp(5, p.getWaktuMulai() != null
                    ? Timestamp.valueOf(p.getWaktuMulai()) : null);
            stmt.setTimestamp(6, p.getWaktuSelesai() != null
                    ? Timestamp.valueOf(p.getWaktuSelesai()) : null);
            stmt.setString(7, p.getLokasiSesi());
            stmt.setString(8, p.getFotoDokumentasi());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        p.setIdPemesanan(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStatus(int idPemesanan, String statusBaru) {
        String sql = "UPDATE pemesanan SET status_pemesanan = ? WHERE id_pemesanan = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, statusBaru);
            stmt.setInt(2, idPemesanan);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Pemesanan p) {
        String sql = "UPDATE pemesanan SET id_murid = ?, id_guru = ?, id_materi = ?, "
                   + "status_pemesanan = ?, waktu_mulai = ?, waktu_selesai = ?, "
                   + "lokasi_sesi = ?, foto_dokumentasi = ? WHERE id_pemesanan = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, p.getIdMurid());
            stmt.setInt(2, p.getIdGuru());
            stmt.setInt(3, p.getIdMateri());
            stmt.setString(4, p.getStatusPemesanan());
            stmt.setTimestamp(5, p.getWaktuMulai() != null
                    ? Timestamp.valueOf(p.getWaktuMulai()) : null);
            stmt.setTimestamp(6, p.getWaktuSelesai() != null
                    ? Timestamp.valueOf(p.getWaktuSelesai()) : null);
            stmt.setString(7, p.getLokasiSesi());
            stmt.setString(8, p.getFotoDokumentasi());
            stmt.setInt(9, p.getIdPemesanan());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM pemesanan WHERE id_pemesanan = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
