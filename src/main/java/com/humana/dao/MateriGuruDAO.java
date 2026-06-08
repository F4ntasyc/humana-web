package com.humana.dao;

import com.humana.model.Guru;
import com.humana.model.Materi;
import com.humana.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO untuk tabel relasi MateriGuru (Many-to-Many).
 * Menghubungkan guru dengan materi yang mereka ampu.
 * Semua operasi database menggunakan try-with-resources.
 */
public class MateriGuruDAO {

    public List<Materi> findMateriByGuruId(int idGuru) {
        String sql = "SELECT m.* FROM materi m "
                   + "JOIN materi_guru mg ON m.id_materi = mg.id_materi "
                   + "WHERE mg.id_guru = ?";
        List<Materi> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idGuru);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Materi materi = new Materi();
                    materi.setIdMateri(rs.getInt("id_materi"));
                    materi.setIdMapel(rs.getInt("id_mapel"));
                    materi.setNamaMateri(rs.getString("nama_materi"));
                    materi.setKelas(rs.getInt("kelas"));
                    materi.setJurusan(rs.getString("jurusan"));
                    materi.setDeskripsi(rs.getString("deskripsi"));
                    list.add(materi);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Guru> findGuruByMateriId(int idMateri) {
        String sql = "SELECT g.* FROM guru g "
                   + "JOIN materi_guru mg ON g.id_guru = mg.id_guru "
                   + "WHERE mg.id_materi = ?";
        List<Guru> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMateri);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
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
                    list.add(guru);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(int idGuru, int idMateri) {
        String sql = "INSERT INTO materi_guru (id_guru, id_materi) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idGuru);
            stmt.setInt(2, idMateri);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int idGuru, int idMateri) {
        String sql = "DELETE FROM materi_guru WHERE id_guru = ? AND id_materi = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idGuru);
            stmt.setInt(2, idMateri);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
