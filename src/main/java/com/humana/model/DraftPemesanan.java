package com.humana.model;

import java.time.LocalDateTime;

/**
 * Model class DraftPemesanan — merepresentasikan draft pemesanan sesi yang belum disubmit.
 * Class baru yang tidak ada di backend mobile, dibuat berdasarkan skema DB.
 *
 * <p>Kolom DB: id_murid (PK FK), draft_data (json), created_at, updated_at</p>
 */
public class DraftPemesanan {

    private int idMurid;           // PK sekaligus FK ke tabel murid
    private String draftData;      // JSON string
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ======== Constructors ========

    public DraftPemesanan() {
    }

    /**
     * Constructor lengkap (untuk mapping dari ResultSet DB).
     */
    public DraftPemesanan(int idMurid, String draftData,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.idMurid = idMurid;
        this.draftData = draftData;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Constructor untuk draft baru.
     */
    public DraftPemesanan(int idMurid, String draftData) {
        this.idMurid = idMurid;
        this.draftData = draftData;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // ======== Getters & Setters ========

    public int getIdMurid() {
        return idMurid;
    }

    public void setIdMurid(int idMurid) {
        this.idMurid = idMurid;
    }

    public String getDraftData() {
        return draftData;
    }

    public void setDraftData(String draftData) {
        this.draftData = draftData;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
