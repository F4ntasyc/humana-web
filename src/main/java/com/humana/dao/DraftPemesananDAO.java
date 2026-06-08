package com.humana.dao;

import com.humana.model.DraftPemesanan;
import com.humana.util.DBConnection;

import java.sql.*;

/**
 * DAO untuk entitas DraftPemesanan.
 * Menggunakan INSERT ... ON DUPLICATE KEY UPDATE untuk operasi upsert
 * karena id_murid adalah PK (satu murid hanya punya satu draft).
 */
public class DraftPemesananDAO {

    public DraftPemesanan findByMuridId(int idMurid) {
        String sql = "SELECT * FROM draft_pemesanan WHERE id_murid = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMurid);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    DraftPemesanan draft = new DraftPemesanan();
                    draft.setIdMurid(rs.getInt("id_murid"));
                    draft.setDraftData(rs.getString("draft_data"));

                    Timestamp tsCreated = rs.getTimestamp("created_at");
                    if (tsCreated != null) {
                        draft.setCreatedAt(tsCreated.toLocalDateTime());
                    }
                    Timestamp tsUpdated = rs.getTimestamp("updated_at");
                    if (tsUpdated != null) {
                        draft.setUpdatedAt(tsUpdated.toLocalDateTime());
                    }
                    return draft;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertOrUpdate(DraftPemesanan draft) {
        // Upsert: INSERT jika belum ada, UPDATE jika sudah ada (berdasarkan PK id_murid)
        String sql = "INSERT INTO draft_pemesanan (id_murid, draft_data, created_at, updated_at) "
                   + "VALUES (?, ?, NOW(), NOW()) "
                   + "ON DUPLICATE KEY UPDATE draft_data = ?, updated_at = NOW()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, draft.getIdMurid());
            stmt.setString(2, draft.getDraftData());
            stmt.setString(3, draft.getDraftData()); // untuk ON DUPLICATE KEY UPDATE

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int idMurid) {
        String sql = "DELETE FROM draft_pemesanan WHERE id_murid = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMurid);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
