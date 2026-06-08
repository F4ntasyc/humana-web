package com.humana.model;

import java.time.LocalDateTime;

/**
 * Model class Pembayaran — merepresentasikan data pembayaran sesi les.
 *
 * <p>Adaptasi dari: backend/src/classes/Pembayaran.js</p>
 * <p>Perbaikan OOP:
 * <ul>
 *   <li>statusPembayaran dan metodePembayaran disimpan sebagai String sesuai DB</li>
 *   <li>konfirmasiBayar() tetap dipertahankan sebagai simple state transition</li>
 * </ul>
 * </p>
 *
 * <p>Kolom DB: id_pembayaran, id_pemesanan (FK), biaya_sesi, biaya_jarak,
 * metode_pembayaran, nominal, status_pembayaran, tanggal_pembayaran</p>
 */
public class Pembayaran {

    // Konstanta status pembayaran
    public static final String STATUS_MENUNGGU = "menunggu";
    public static final String STATUS_LUNAS = "lunas";

    private int idPembayaran;
    private int idPemesanan;      // FK ke tabel pemesanan
    private int biayaSesi;
    private int biayaJarak;
    private int nominal;          // total pembayaran
    private String metodePembayaran;
    private String statusPembayaran;
    private LocalDateTime tanggalPembayaran;

    // ======== Constructors ========

    public Pembayaran() {
        this.metodePembayaran = STATUS_MENUNGGU;
        this.statusPembayaran = STATUS_MENUNGGU;
    }

    /**
     * Constructor lengkap (untuk mapping dari ResultSet DB).
     */
    public Pembayaran(int idPembayaran, int idPemesanan, int biayaSesi, int biayaJarak,
                      int nominal, String metodePembayaran, String statusPembayaran,
                      LocalDateTime tanggalPembayaran) {
        this.idPembayaran = idPembayaran;
        this.idPemesanan = idPemesanan;
        this.biayaSesi = biayaSesi;
        this.biayaJarak = biayaJarak;
        this.nominal = nominal;
        this.metodePembayaran = metodePembayaran;
        this.statusPembayaran = statusPembayaran;
        this.tanggalPembayaran = tanggalPembayaran;
    }

    /**
     * Constructor untuk pembayaran baru (tanpa id, status default 'menunggu').
     * Adaptasi dari Pembayaran.js constructor.
     */
    public Pembayaran(int idPemesanan, int biayaSesi, int biayaJarak, int nominal) {
        this.idPemesanan = idPemesanan;
        this.biayaSesi = biayaSesi;
        this.biayaJarak = biayaJarak;
        this.nominal = nominal;
        this.metodePembayaran = STATUS_MENUNGGU;
        this.statusPembayaran = STATUS_MENUNGGU;
    }

    // ======== Business Methods (dari Pembayaran.js) ========

    /**
     * Cek apakah pembayaran sudah lunas.
     * Adaptasi dari Pembayaran.js isBayar().
     */
    public boolean isLunas() {
        return STATUS_LUNAS.equals(statusPembayaran);
    }

    /**
     * Konfirmasi pembayaran — ubah status ke 'lunas' dan catat metode serta tanggal.
     * Adaptasi dari Pembayaran.js konfirmasiBayar().
     *
     * @param metodeTerpilih metode pembayaran yang dipilih
     * @return true jika berhasil (status sebelumnya 'menunggu')
     */
    public boolean konfirmasiBayar(String metodeTerpilih) {
        if (STATUS_MENUNGGU.equals(statusPembayaran)) {
            this.statusPembayaran = STATUS_LUNAS;
            this.metodePembayaran = metodeTerpilih;
            this.tanggalPembayaran = LocalDateTime.now();
            return true;
        }
        return false;
    }

    // ======== Getters & Setters ========

    public int getIdPembayaran() {
        return idPembayaran;
    }

    public void setIdPembayaran(int idPembayaran) {
        this.idPembayaran = idPembayaran;
    }

    public int getIdPemesanan() {
        return idPemesanan;
    }

    public void setIdPemesanan(int idPemesanan) {
        this.idPemesanan = idPemesanan;
    }

    public int getBiayaSesi() {
        return biayaSesi;
    }

    public void setBiayaSesi(int biayaSesi) {
        this.biayaSesi = biayaSesi;
    }

    public int getBiayaJarak() {
        return biayaJarak;
    }

    public void setBiayaJarak(int biayaJarak) {
        this.biayaJarak = biayaJarak;
    }

    public int getNominal() {
        return nominal;
    }

    public void setNominal(int nominal) {
        this.nominal = nominal;
    }

    public String getMetodePembayaran() {
        return metodePembayaran;
    }

    public void setMetodePembayaran(String metodePembayaran) {
        this.metodePembayaran = metodePembayaran;
    }

    public String getStatusPembayaran() {
        return statusPembayaran;
    }

    public void setStatusPembayaran(String statusPembayaran) {
        this.statusPembayaran = statusPembayaran;
    }

    public LocalDateTime getTanggalPembayaran() {
        return tanggalPembayaran;
    }

    public void setTanggalPembayaran(LocalDateTime tanggalPembayaran) {
        this.tanggalPembayaran = tanggalPembayaran;
    }
}
