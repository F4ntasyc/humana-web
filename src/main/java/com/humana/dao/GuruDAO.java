package com.humana.dao;

import com.humana.model.Guru;
import com.humana.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO untuk entitas Guru.
 * Semua operasi database menggunakan try-with-resources untuk keamanan koneksi.
 */
public class GuruDAO {

    // ======== Helper: Mapping ResultSet ke objek Guru ========

    /**
     * Mengkonversi baris ResultSet menjadi objek Guru.
     */
    private Guru mapResultSetToGuru(ResultSet rs) throws SQLException {
        Guru guru = new Guru();
        guru.setId(rs.getInt("id_guru"));
        guru.setNamaUser(rs.getString("nama_guru"));
        guru.setEmail(rs.getString("email_guru"));
        guru.setPassword(rs.getString("password"));
        guru.setNoTelepon(rs.getString("no_telepon"));
        guru.setRating(rs.getBigDecimal("rating"));
        guru.setJenisKelamin(rs.getString("jenis_kelamin"));
        guru.setAlamat(rs.getString("alamat"));
        guru.setUsername(rs.getString("username"));
        guru.setActive(rs.getInt("is_active") == 1);
        return guru;
    }

    // ======== CRUD Operations ========

    public Guru findById(int id) {
        String sql = "SELECT * FROM guru WHERE id_guru = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToGuru(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Guru findByEmail(String email) {
        String sql = "SELECT * FROM guru WHERE email_guru = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToGuru(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Guru findByUsername(String username) {
        String sql = "SELECT * FROM guru WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToGuru(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Guru> findAll() {
        String sql = "SELECT * FROM guru";
        List<Guru> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToGuru(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(Guru guru) {
        String sql = "INSERT INTO guru (nama_guru, email_guru, password, no_telepon, "
                   + "rating, jenis_kelamin, alamat, username, is_active) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, guru.getNamaUser());
            stmt.setString(2, guru.getEmail());
            stmt.setString(3, guru.getPassword());
            stmt.setString(4, guru.getNoTelepon());
            stmt.setBigDecimal(5, guru.getRating() != null ? guru.getRating() : BigDecimal.ZERO);
            stmt.setString(6, guru.getJenisKelamin());
            stmt.setString(7, guru.getAlamat());
            stmt.setString(8, guru.getUsername());
            stmt.setInt(9, guru.isActive() ? 1 : 0);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        guru.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Guru guru) {
        String sql = "UPDATE guru SET nama_guru = ?, email_guru = ?, password = ?, "
                   + "no_telepon = ?, jenis_kelamin = ?, alamat = ?, username = ?, is_active = ? "
                   + "WHERE id_guru = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, guru.getNamaUser());
            stmt.setString(2, guru.getEmail());
            stmt.setString(3, guru.getPassword());
            stmt.setString(4, guru.getNoTelepon());
            stmt.setString(5, guru.getJenisKelamin());
            stmt.setString(6, guru.getAlamat());
            stmt.setString(7, guru.getUsername());
            stmt.setInt(8, guru.isActive() ? 1 : 0);
            stmt.setInt(9, guru.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateRating(int idGuru, double rating) {
        String sql = "UPDATE guru SET rating = ? WHERE id_guru = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, rating);
            stmt.setInt(2, idGuru);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM guru WHERE id_guru = ?";
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
