package com.humana.model;

import java.time.LocalDateTime;

/**
 * Model class Pemesanan — merepresentasikan pemesanan sesi les privat.
 *
 * <p>Adaptasi dari: backend/src/classes/PemesananSesi.js</p>
 * <p>Perbaikan OOP:
 * <ul>
 *   <li>Referensi objek murid/guru diganti FK id (int) sesuai DB relasional</li>
 *   <li>toJSON() dihapus (tidak relevan di Java Servlet/JSP)</li>
 *   <li>hitungDurasiJam() dan hitungTotalBiaya() tetap dipertahankan sebagai utility model</li>
 *   <li>statusPemesanan disimpan sebagai String sesuai DB</li>
 *   <li>Ditambahkan field fotoDokumentasi sesuai skema DB</li>
 * </ul>
 * </p>
 *
 * <p>Kolom DB: id_pemesanan, id_murid (FK), id_guru (FK), id_materi (FK),
 * status_pemesanan, waktu_mulai, waktu_selesai, lokasi_sesi, foto_dokumentasi</p>
 */
public class Pemesanan {

    // Konstanta status pemesanan (sesuai panduan proyek)
    public static final String STATUS_MENUNGGU_KONFIRMASI = "menunggu konfirmasi";
    public static final String STATUS_DIKONFIRMASI = "dikonfirmasi";
    public static final String STATUS_BERLANGSUNG = "berlangsung";
    public static final String STATUS_SELESAI = "selesai";
    public static final String STATUS_DIBATALKAN = "dibatalkan";

    private int idPemesanan;
    private int idMurid;          // FK ke tabel murid
    private int idGuru;           // FK ke tabel guru
    private int idMateri;         // FK ke tabel materi
    private String statusPemesanan;
    private LocalDateTime waktuMulai;
    private LocalDateTime waktuSelesai;
    private String lokasiSesi;
    private String fotoDokumentasi;

    // ======== Constructors ========

    public Pemesanan() {
        this.statusPemesanan = STATUS_MENUNGGU_KONFIRMASI;
    }

    /**
     * Constructor lengkap (untuk mapping dari ResultSet DB).
     */
    public Pemesanan(int idPemesanan, int idMurid, int idGuru, int idMateri,
                     String statusPemesanan, LocalDateTime waktuMulai,
                     LocalDateTime waktuSelesai, String lokasiSesi, String fotoDokumentasi) {
        this.idPemesanan = idPemesanan;
        this.idMurid = idMurid;
        this.idGuru = idGuru;
        this.idMateri = idMateri;
        this.statusPemesanan = statusPemesanan;
        this.waktuMulai = waktuMulai;
        this.waktuSelesai = waktuSelesai;
        this.lokasiSesi = lokasiSesi;
        this.fotoDokumentasi = fotoDokumentasi;
    }

    /**
     * Constructor tanpa id (untuk insert baru — status default 'menunggu konfirmasi').
     */
    public Pemesanan(int idMurid, int idGuru, int idMateri,
                     LocalDateTime waktuMulai, LocalDateTime waktuSelesai, String lokasiSesi) {
        this.idMurid = idMurid;
        this.idGuru = idGuru;
        this.idMateri = idMateri;
        this.statusPemesanan = STATUS_MENUNGGU_KONFIRMASI;
        this.waktuMulai = waktuMulai;
        this.waktuSelesai = waktuSelesai;
        this.lokasiSesi = lokasiSesi;
    }

    // ======== Business Methods (dari PemesananSesi.js) ========

    /**
     * Menghitung durasi sesi dalam jam.
     * Adaptasi dari PemesananSesi.js HitungDurasiJam().
     *
     * @return durasi dalam jam (minimal 0)
     */
    public double hitungDurasiJam() {
        if (waktuMulai == null || waktuSelesai == null) return 0;

        java.time.Duration duration = java.time.Duration.between(waktuMulai, waktuSelesai);
        double durasiJam = duration.toMinutes() / 60.0;
        return Math.max(0, durasiJam);
    }

    /**
     * Menghitung total biaya berdasarkan durasi dan jarak.
     * Tarif: Rp 30.000/jam (minimum 1 jam), Rp 3.000/km.
     * Adaptasi dari PemesananSesi.js HitungTotalBiaya().
     *
     * @param jarakKm jarak guru ke lokasi sesi dalam km
     * @return array [biayaSesi, biayaJarak, totalPembayaran]
     */
    public int[] hitungTotalBiaya(double jarakKm) {
        int tarifPerJam = 30000;
        int tarifPerKm = 3000;

        double totalJam = Math.max(1, hitungDurasiJam());

        int biayaSesi = (int) Math.round(totalJam * tarifPerJam);
        int biayaJarak = (int) (Math.ceil(jarakKm * tarifPerKm / 500.0) * 500);
        int totalPembayaran = biayaSesi + biayaJarak;

        return new int[]{biayaSesi, biayaJarak, totalPembayaran};
    }

    /**
     * Konfirmasi pemesanan (ubah status menjadi dikonfirmasi).
     */
    public void konfirmasi() {
        this.statusPemesanan = STATUS_DIKONFIRMASI;
    }

    /**
     * Batalkan pemesanan (ubah status menjadi dibatalkan).
     */
    public void batalkan() {
        this.statusPemesanan = STATUS_DIBATALKAN;
    }

    // ======== Getters & Setters ========

    public int getIdPemesanan() {
        return idPemesanan;
    }

    public void setIdPemesanan(int idPemesanan) {
        this.idPemesanan = idPemesanan;
    }

    public int getIdMurid() {
        return idMurid;
    }

    public void setIdMurid(int idMurid) {
        this.idMurid = idMurid;
    }

    public int getIdGuru() {
        return idGuru;
    }

    public void setIdGuru(int idGuru) {
        this.idGuru = idGuru;
    }

    public int getIdMateri() {
        return idMateri;
    }

    public void setIdMateri(int idMateri) {
        this.idMateri = idMateri;
    }

    public String getStatusPemesanan() {
        return statusPemesanan;
    }

    public void setStatusPemesanan(String statusPemesanan) {
        this.statusPemesanan = statusPemesanan;
    }

    public LocalDateTime getWaktuMulai() {
        return waktuMulai;
    }

    public void setWaktuMulai(LocalDateTime waktuMulai) {
        this.waktuMulai = waktuMulai;
    }

    public LocalDateTime getWaktuSelesai() {
        return waktuSelesai;
    }

    public void setWaktuSelesai(LocalDateTime waktuSelesai) {
        this.waktuSelesai = waktuSelesai;
    }

    public String getLokasiSesi() {
        return lokasiSesi;
    }

    public void setLokasiSesi(String lokasiSesi) {
        this.lokasiSesi = lokasiSesi;
    }

    public String getFotoDokumentasi() {
        return fotoDokumentasi;
    }

    public void setFotoDokumentasi(String fotoDokumentasi) {
        this.fotoDokumentasi = fotoDokumentasi;
    }
}
