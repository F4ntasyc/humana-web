package com.humana.dao;

import com.humana.model.Murid;
import com.humana.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO untuk entitas Murid.
 * Semua operasi database menggunakan try-with-resources untuk keamanan koneksi.
 */
public class MuridDAO {

    // ======== Helper: Mapping ResultSet ke objek Murid ========

    private Murid mapResultSetToMurid(ResultSet rs) throws SQLException {
        Murid murid = new Murid();
        murid.setId(rs.getInt("id_murid"));
        murid.setNamaUser(rs.getString("nama_murid"));
        murid.setEmail(rs.getString("email"));
        murid.setUsername(rs.getString("username"));
        murid.setPassword(rs.getString("password"));
        murid.setNoTelepon(rs.getString("no_telepon"));
        murid.setJenisKelamin(rs.getString("jenis_kelamin"));
        murid.setAlamat(rs.getString("alamat"));
        murid.setKelas(rs.getInt("kelas"));
        murid.setJurusan(rs.getString("jurusan"));
        return murid;
    }

    // ======== CRUD Operations ========

    public Murid findById(int id) {
        String sql = "SELECT * FROM Murid WHERE id_murid = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMurid(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Murid findByEmail(String email) {
        String sql = "SELECT * FROM Murid WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMurid(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Murid findByUsername(String username) {
        String sql = "SELECT * FROM Murid WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMurid(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Murid> findAll() {
        String sql = "SELECT * FROM Murid";
        List<Murid> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToMurid(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(Murid murid) {
        String sql = "INSERT INTO Murid (nama_murid, email, username, password, "
                   + "no_telepon, jenis_kelamin, alamat, kelas, jurusan) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, murid.getNamaUser());
            stmt.setString(2, murid.getEmail());
            stmt.setString(3, murid.getUsername());
            stmt.setString(4, murid.getPassword());
            stmt.setString(5, murid.getNoTelepon());
            stmt.setString(6, murid.getJenisKelamin());
            stmt.setString(7, murid.getAlamat());
            stmt.setInt(8, murid.getKelas());
            stmt.setString(9, murid.getJurusan());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        murid.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Murid murid) {
        String sql = "UPDATE Murid SET nama_murid = ?, email = ?, username = ?, password = ?, "
                   + "no_telepon = ?, jenis_kelamin = ?, alamat = ?, kelas = ?, jurusan = ? "
                   + "WHERE id_murid = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, murid.getNamaUser());
            stmt.setString(2, murid.getEmail());
            stmt.setString(3, murid.getUsername());
            stmt.setString(4, murid.getPassword());
            stmt.setString(5, murid.getNoTelepon());
            stmt.setString(6, murid.getJenisKelamin());
            stmt.setString(7, murid.getAlamat());
            stmt.setInt(8, murid.getKelas());
            stmt.setString(9, murid.getJurusan());
            stmt.setInt(10, murid.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Murid WHERE id_murid = ?";
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
