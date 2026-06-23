package com.humana.dao;

import com.humana.model.Portfolio;
import com.humana.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PortfolioDAO {

    public List<Portfolio> findByGuruId(int idGuru) {
        String sql = "SELECT * FROM Portfolio WHERE id_guru = ? ORDER BY tanggal_mulai DESC";
        List<Portfolio> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idGuru);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(Portfolio p) {
        String sql = "INSERT INTO Portfolio (id_guru, judul, deskripsi, tipe_portfolio, bukti, tanggal_mulai, tanggal_selesai) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, p.getIdGuru());
            stmt.setString(2, p.getJudul());
            stmt.setString(3, p.getDeskripsi());
            stmt.setString(4, p.getTipePortfolio());
            stmt.setString(5, p.getBukti());
            stmt.setDate(6, p.getTanggalMulai());
            stmt.setDate(7, p.getTanggalSelesai());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int idPortfolio, int idGuru) {
        String sql = "DELETE FROM Portfolio WHERE id_portfolio = ? AND id_guru = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPortfolio);
            stmt.setInt(2, idGuru);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Portfolio mapRow(ResultSet rs) throws SQLException {
        Portfolio p = new Portfolio();
        p.setIdPortfolio(rs.getInt("id_portfolio"));
        p.setIdGuru(rs.getInt("id_guru"));
        p.setJudul(rs.getString("judul"));
        p.setDeskripsi(rs.getString("deskripsi"));
        p.setTipePortfolio(rs.getString("tipe_portfolio"));
        p.setBukti(rs.getString("bukti"));
        p.setTanggalMulai(rs.getDate("tanggal_mulai"));
        p.setTanggalSelesai(rs.getDate("tanggal_selesai"));
        return p;
    }
}
