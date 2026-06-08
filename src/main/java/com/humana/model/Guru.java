package com.humana.model;

import java.math.BigDecimal;

/**
 * Model class Guru — merepresentasikan data guru les privat.
 * Extends {@link User} dan implements {@link Notifiable}.
 *
 * <p>Adaptasi dari: backend/src/classes/Guru.js</p>
 * <p>Perbaikan OOP:
 * <ul>
 *   <li>In-memory lists (portofolio, daftarMateri, riwayatSesi, daftarPesanan, daftarFeedback)
 *       dihapus dari model — menjadi tanggung jawab DAO layer</li>
 *   <li>Business logic (konfirmasiPesanan) dipindahkan ke servlet/service layer</li>
 *   <li>getRating() menjadi simple getter dari DB field, bukan calculated</li>
 *   <li>jenisKelamin disimpan sebagai String ("L"/"P") sesuai DB</li>
 * </ul>
 * </p>
 *
 * <p>Kolom DB: id_guru, nama_guru, email_guru, password, no_telepon,
 * rating, jenis_kelamin, alamat, username, is_active</p>
 */
public class Guru extends User implements Notifiable {

    private String noTelepon;
    private BigDecimal rating;
    private String jenisKelamin; // "L" atau "P"
    private String alamat;
    private boolean isActive;

    // ======== Constructors ========

    public Guru() {
        super();
    }

    /**
     * Constructor lengkap (untuk mapping dari ResultSet DB).
     */
    public Guru(int id, String username, String email, String password, String namaUser,
                String noTelepon, BigDecimal rating, String jenisKelamin,
                String alamat, boolean isActive) {
        super(id, username, email, password, namaUser);
        this.noTelepon = noTelepon;
        this.rating = rating;
        this.jenisKelamin = jenisKelamin;
        this.alamat = alamat;
        this.isActive = isActive;
    }

    /**
     * Constructor untuk registrasi baru (tanpa id, rating default 0).
     */
    public Guru(String username, String email, String password, String namaUser,
                String noTelepon, String jenisKelamin, String alamat) {
        super(username, email, password, namaUser);
        this.noTelepon = noTelepon;
        this.rating = BigDecimal.ZERO;
        this.jenisKelamin = jenisKelamin;
        this.alamat = alamat;
        this.isActive = true;
    }

    // ======== Abstract & Interface Implementations ========

    @Override
    public String getRole() {
        return "GURU";
    }

    @Override
    public void receiveNotification(String message) {
        // Implementasi notifikasi — untuk saat ini hanya log ke console
        System.out.println("Notifikasi Guru [" + getNamaUser() + "]: " + message);
    }

    // ======== Utility Methods ========

    /**
     * Mendapatkan label jenis kelamin lengkap.
     * "L" → "Laki-laki", "P" → "Perempuan"
     */
    public String getLabelJenisKelamin() {
        if ("L".equals(jenisKelamin)) return "Laki-laki";
        if ("P".equals(jenisKelamin)) return "Perempuan";
        return jenisKelamin != null ? jenisKelamin : "-";
    }

    // ======== Getters & Setters ========

    public String getNoTelepon() {
        return noTelepon;
    }

    public void setNoTelepon(String noTelepon) {
        this.noTelepon = noTelepon;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public String getJenisKelamin() {
        return jenisKelamin;
    }

    public void setJenisKelamin(String jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }
}
