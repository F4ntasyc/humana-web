package com.humana.model;

import java.sql.Date;

/**
 * Model Portfolio guru.
 */
public class Portfolio {

    private int idPortfolio;
    private int idGuru;
    private String judul;
    private String deskripsi;
    private String tipePortfolio;
    private String bukti;
    private Date tanggalMulai;
    private Date tanggalSelesai;

    public int getIdPortfolio() { return idPortfolio; }
    public void setIdPortfolio(int idPortfolio) { this.idPortfolio = idPortfolio; }
    public int getIdGuru() { return idGuru; }
    public void setIdGuru(int idGuru) { this.idGuru = idGuru; }
    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }
    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    public String getTipePortfolio() { return tipePortfolio; }
    public void setTipePortfolio(String tipePortfolio) { this.tipePortfolio = tipePortfolio; }
    public String getBukti() { return bukti; }
    public void setBukti(String bukti) { this.bukti = bukti; }
    public Date getTanggalMulai() { return tanggalMulai; }
    public void setTanggalMulai(Date tanggalMulai) { this.tanggalMulai = tanggalMulai; }
    public Date getTanggalSelesai() { return tanggalSelesai; }
    public void setTanggalSelesai(Date tanggalSelesai) { this.tanggalSelesai = tanggalSelesai; }
}
