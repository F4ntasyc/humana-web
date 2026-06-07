package com.humana.dao;

import com.humana.model.Pembayaran;
import com.humana.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementasi DAO untuk entitas Pembayaran.
 * Semua operasi database menggunakan try-with-resources.
 */
public class PembayaranDAOImpl implements PembayaranDAO {

    private Pembayaran mapResultSetToPembayaran(ResultSet rs) throws SQLException {
        Pembayaran p = new Pembayaran();
        p.setIdPembayaran(rs.getInt("id_pembayaran"));
        p.setIdPemesanan(rs.getInt("id_pemesanan"));
        p.setBiayaSesi(rs.getInt("biaya_sesi"));
        p.setBiayaJarak(rs.getInt("biaya_jarak"));
        p.setNominal(rs.getInt("nominal"));
        p.setMetodePembayaran(rs.getString("metode_pembayaran"));
        p.setStatusPembayaran(rs.getString("status_pembayaran"));

        Timestamp ts = rs.getTimestamp("tanggal_pembayaran");
        if (ts != null) {
            p.setTanggalPembayaran(ts.toLocalDateTime());
        }
        return p;
    }

    @Override
    public Pembayaran findById(int id) {
        String sql = "SELECT * FROM pembayaran WHERE id_pembayaran = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPembayaran(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Pembayaran findByPemesananId(int idPemesanan) {
        String sql = "SELECT * FROM pembayaran WHERE id_pemesanan = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPemesanan);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPembayaran(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Pembayaran> findAll() {
        String sql = "SELECT * FROM pembayaran";
        List<Pembayaran> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToPembayaran(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean insert(Pembayaran p) {
        String sql = "INSERT INTO pembayaran (id_pemesanan, biaya_sesi, biaya_jarak, "
                   + "nominal, metode_pembayaran, status_pembayaran, tanggal_pembayaran) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, p.getIdPemesanan());
            stmt.setInt(2, p.getBiayaSesi());
            stmt.setInt(3, p.getBiayaJarak());
            stmt.setInt(4, p.getNominal());
            stmt.setString(5, p.getMetodePembayaran());
            stmt.setString(6, p.getStatusPembayaran());
            stmt.setTimestamp(7, p.getTanggalPembayaran() != null
                    ? Timestamp.valueOf(p.getTanggalPembayaran()) : null);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        p.setIdPembayaran(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateStatus(int idPembayaran, String statusBaru, String metodePembayaran) {
        String sql = "UPDATE pembayaran SET status_pembayaran = ?, metode_pembayaran = ?, "
                   + "tanggal_pembayaran = ? WHERE id_pembayaran = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, statusBaru);
            stmt.setString(2, metodePembayaran);
            stmt.setTimestamp(3, Timestamp.valueOf(java.time.LocalDateTime.now()));
            stmt.setInt(4, idPembayaran);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM pembayaran WHERE id_pembayaran = ?";
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
