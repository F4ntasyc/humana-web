package com.humana.model;

/**
 * Model class Feedback — merepresentasikan feedback/ulasan dari murid untuk sesi les.
 *
 * <p>Adaptasi dari: backend/src/classes/Feedback.js</p>
 * <p>Perbaikan OOP:
 * <ul>
 *   <li>Ditambahkan field idPemesanan (FK) yang ada di DB tapi tidak ada di JS class</li>
 *   <li>Full getter/setter</li>
 * </ul>
 * </p>
 *
 * <p>Kolom DB: id_feedback, id_pemesanan (FK), komentar, rating (tinyint)</p>
 */
public class Feedback {

    private int idFeedback;
    private int idPemesanan;   // FK ke tabel pemesanan
    private String komentar;
    private int rating;        // 1-5

    // ======== Constructors ========

    public Feedback() {
    }

    /**
     * Constructor lengkap (untuk mapping dari ResultSet DB).
     */
    public Feedback(int idFeedback, int idPemesanan, String komentar, int rating) {
        this.idFeedback = idFeedback;
        this.idPemesanan = idPemesanan;
        this.komentar = komentar;
        this.rating = rating;
    }

    /**
     * Constructor tanpa id (untuk insert baru).
     */
    public Feedback(int idPemesanan, String komentar, int rating) {
        this.idPemesanan = idPemesanan;
        this.komentar = komentar;
        this.rating = rating;
    }

    // ======== Getters & Setters ========

    public int getIdFeedback() {
        return idFeedback;
    }

    public void setIdFeedback(int idFeedback) {
        this.idFeedback = idFeedback;
    }

    public int getIdPemesanan() {
        return idPemesanan;
    }

    public void setIdPemesanan(int idPemesanan) {
        this.idPemesanan = idPemesanan;
    }

    public String getKomentar() {
        return komentar;
    }

    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
