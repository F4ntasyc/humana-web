package com.humana.dao;

import com.humana.model.MataPelajaran;
import com.humana.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO untuk entitas MataPelajaran.
 * Semua operasi database menggunakan try-with-resources.
 */
public class MataPelajaranDAO {

    private MataPelajaran mapResultSetToMataPelajaran(ResultSet rs) throws SQLException {
        MataPelajaran mp = new MataPelajaran();
        mp.setIdMapel(rs.getInt("id_mapel"));
        mp.setNamaMapel(rs.getString("nama_mapel"));
        mp.setJenjang(rs.getString("jenjang"));
        return mp;
    }

    public MataPelajaran findById(int id) {
        String sql = "SELECT * FROM mata_pelajaran WHERE id_mapel = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMataPelajaran(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<MataPelajaran> findAll() {
        String sql = "SELECT * FROM mata_pelajaran";
        List<MataPelajaran> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToMataPelajaran(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<MataPelajaran> findByJenjang(String jenjang) {
        String sql = "SELECT * FROM mata_pelajaran WHERE jenjang = ?";
        List<MataPelajaran> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, jenjang);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToMataPelajaran(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(MataPelajaran mp) {
        String sql = "INSERT INTO mata_pelajaran (nama_mapel, jenjang) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, mp.getNamaMapel());
            stmt.setString(2, mp.getJenjang());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        mp.setIdMapel(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(MataPelajaran mp) {
        String sql = "UPDATE mata_pelajaran SET nama_mapel = ?, jenjang = ? WHERE id_mapel = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mp.getNamaMapel());
            stmt.setString(2, mp.getJenjang());
            stmt.setInt(3, mp.getIdMapel());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM mata_pelajaran WHERE id_mapel = ?";
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
