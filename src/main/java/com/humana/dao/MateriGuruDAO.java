package com.humana.dao;

import com.humana.model.Guru;
import com.humana.model.Materi;
import com.humana.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * DAO untuk tabel relasi MateriGuru (Many-to-Many).
 */
public class MateriGuruDAO {

    public List<Materi> findMateriByGuruId(int idGuru) {
        String sql = "SELECT m.*, mp.nama_mapel, mp.jenjang FROM Materi m "
                + "JOIN MateriGuru mg ON m.id_materi = mg.id_materi "
                + "JOIN MataPelajaran mp ON m.id_mapel = mp.id_mapel "
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

    public List<MateriGuruDetail> findMateriDetailByGuruId(int idGuru) {
        String sql = "SELECT m.id_materi, m.nama_materi, m.kelas, mp.nama_mapel, mp.jenjang "
                + "FROM MateriGuru mg "
                + "JOIN Materi m ON mg.id_materi = m.id_materi "
                + "JOIN MataPelajaran mp ON m.id_mapel = mp.id_mapel "
                + "WHERE mg.id_guru = ?";
        List<MateriGuruDetail> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idGuru);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MateriGuruDetail d = new MateriGuruDetail();
                    d.idMateri = rs.getInt("id_materi");
                    d.namaMateri = rs.getString("nama_materi");
                    d.kelas = rs.getInt("kelas");
                    d.namaMapel = rs.getString("nama_mapel");
                    d.jenjang = rs.getString("jenjang");
                    list.add(d);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Guru> findGuruByMateriId(int idMateri) {
        String sql = "SELECT g.* FROM Guru g "
                + "JOIN MateriGuru mg ON g.id_guru = mg.id_guru "
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
        String sql = "INSERT INTO MateriGuru (id_guru, id_materi) VALUES (?, ?)";
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
        String sql = "DELETE FROM MateriGuru WHERE id_guru = ? AND id_materi = ?";
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

    /** Sinkronkan daftar materi guru — seperti mobile simpanMateriGuru. */
    public boolean syncMateriGuru(int idGuru, List<Integer> idMateriList) throws SQLException {
        Set<Integer> clean = new HashSet<>();
        if (idMateriList != null) {
            for (Integer id : idMateriList) {
                if (id != null && id > 0) clean.add(id);
            }
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (clean.isEmpty()) {
                    try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM MateriGuru WHERE id_guru = ?")) {
                        stmt.setInt(1, idGuru);
                        stmt.executeUpdate();
                    }
                } else {
                    String placeholders = String.join(",", clean.stream().map(i -> "?").toList());
                    String deleteSql = "DELETE FROM MateriGuru WHERE id_guru = ? AND id_materi NOT IN (" + placeholders + ")";
                    try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
                        stmt.setInt(1, idGuru);
                        int idx = 2;
                        for (int id : clean) {
                            stmt.setInt(idx++, id);
                        }
                        stmt.executeUpdate();
                    }
                    for (int idMateri : clean) {
                        try (PreparedStatement stmt = conn.prepareStatement(
                                "INSERT IGNORE INTO MateriGuru (id_guru, id_materi) VALUES (?, ?)")) {
                            stmt.setInt(1, idGuru);
                            stmt.setInt(2, idMateri);
                            stmt.executeUpdate();
                        }
                    }
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public static class MateriGuruDetail {
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
