package com.humana.dao;

import com.humana.model.Materi;
import com.humana.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO untuk entitas Materi.
 */
public class MateriDAO {

    private Materi mapResultSetToMateri(ResultSet rs) throws SQLException {
        Materi materi = new Materi();
        materi.setIdMateri(rs.getInt("id_materi"));
        materi.setIdMapel(rs.getInt("id_mapel"));
        materi.setNamaMateri(rs.getString("nama_materi"));
        materi.setKelas(rs.getInt("kelas"));
        materi.setJurusan(rs.getString("jurusan"));
        materi.setDeskripsi(rs.getString("deskripsi"));
        return materi;
    }

    public Materi findById(int id) {
        String sql = "SELECT * FROM Materi WHERE id_materi = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMateri(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Materi> findAll() {
        String sql = "SELECT * FROM Materi";
        List<Materi> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToMateri(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Materi> findByMapelId(int idMapel) {
        String sql = "SELECT * FROM Materi WHERE id_mapel = ?";
        List<Materi> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMapel);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToMateri(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Materi> findByKelasAndJurusan(int kelas, String jurusan) {
        String sql = "SELECT * FROM Materi WHERE kelas = ? AND jurusan = ?";
        List<Materi> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, kelas);
            stmt.setString(2, jurusan);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToMateri(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(Materi materi) {
        String sql = "INSERT INTO Materi (id_mapel, nama_materi, kelas, jurusan, deskripsi) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, materi.getIdMapel());
            stmt.setString(2, materi.getNamaMateri());
            stmt.setInt(3, materi.getKelas());
            stmt.setString(4, materi.getJurusan());
            stmt.setString(5, materi.getDeskripsi());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        materi.setIdMateri(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Materi materi) {
        String sql = "UPDATE Materi SET id_mapel = ?, nama_materi = ?, kelas = ?, jurusan = ?, deskripsi = ? WHERE id_materi = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, materi.getIdMapel());
            stmt.setString(2, materi.getNamaMateri());
            stmt.setInt(3, materi.getKelas());
            stmt.setString(4, materi.getJurusan());
            stmt.setString(5, materi.getDeskripsi());
            stmt.setInt(6, materi.getIdMateri());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Materi WHERE id_materi = ?";
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
