package com.humana.model;

/**
 * Model class MataPelajaran — merepresentasikan mata pelajaran.
 * Class baru yang tidak ada di backend mobile, dibuat berdasarkan skema DB.
 *
 * <p>Kolom DB: id_mapel, nama_mapel, jenjang (enum 'SD','SMP','SMA','SMK')</p>
 */
public class MataPelajaran {

    private int idMapel;
    private String namaMapel;
    private String jenjang;    // "SD", "SMP", "SMA", atau "SMK"

    // ======== Constructors ========

    public MataPelajaran() {
    }

    /**
     * Constructor lengkap (untuk mapping dari ResultSet DB).
     */
    public MataPelajaran(int idMapel, String namaMapel, String jenjang) {
        this.idMapel = idMapel;
        this.namaMapel = namaMapel;
        this.jenjang = jenjang;
    }

    /**
     * Constructor tanpa id (untuk insert baru).
     */
    public MataPelajaran(String namaMapel, String jenjang) {
        this.namaMapel = namaMapel;
        this.jenjang = jenjang;
    }

    // ======== Getters & Setters ========

    public int getIdMapel() {
        return idMapel;
    }

    public void setIdMapel(int idMapel) {
        this.idMapel = idMapel;
    }

    public String getNamaMapel() {
        return namaMapel;
    }

    public void setNamaMapel(String namaMapel) {
        this.namaMapel = namaMapel;
    }

    public String getJenjang() {
        return jenjang;
    }

    public void setJenjang(String jenjang) {
        this.jenjang = jenjang;
    }
}
