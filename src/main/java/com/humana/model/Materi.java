package com.humana.model;

/**
 * Model class Materi — merepresentasikan materi pelajaran yang tersedia.
 *
 * <p>Adaptasi dari: backend/src/classes/Materi.js</p>
 * <p>Perbaikan OOP:
 * <ul>
 *   <li>Ditambahkan field idMapel (FK) yang ada di DB tapi tidak ada di JS class</li>
 *   <li>Ditambahkan field jurusan sesuai skema DB</li>
 *   <li>Full getter/setter untuk semua field</li>
 * </ul>
 * </p>
 *
 * <p>Kolom DB: id_materi, id_mapel (FK), nama_materi, kelas, jurusan, deskripsi</p>
 */
public class Materi {

    private int idMateri;
    private int idMapel;       // FK ke tabel mata_pelajaran
    private String namaMateri;
    private int kelas;
    private String jurusan;
    private String deskripsi;

    // ======== Constructors ========

    public Materi() {
    }

    /**
     * Constructor lengkap (untuk mapping dari ResultSet DB).
     */
    public Materi(int idMateri, int idMapel, String namaMateri, int kelas,
                  String jurusan, String deskripsi) {
        this.idMateri = idMateri;
        this.idMapel = idMapel;
        this.namaMateri = namaMateri;
        this.kelas = kelas;
        this.jurusan = jurusan;
        this.deskripsi = deskripsi;
    }

    /**
     * Constructor tanpa id (untuk insert baru).
     */
    public Materi(int idMapel, String namaMateri, int kelas, String jurusan, String deskripsi) {
        this.idMapel = idMapel;
        this.namaMateri = namaMateri;
        this.kelas = kelas;
        this.jurusan = jurusan;
        this.deskripsi = deskripsi;
    }

    // ======== Getters & Setters ========

    public int getIdMateri() {
        return idMateri;
    }

    public void setIdMateri(int idMateri) {
        this.idMateri = idMateri;
    }

    public int getIdMapel() {
        return idMapel;
    }

    public void setIdMapel(int idMapel) {
        this.idMapel = idMapel;
    }

    public String getNamaMateri() {
        return namaMateri;
    }

    public void setNamaMateri(String namaMateri) {
        this.namaMateri = namaMateri;
    }

    public int getKelas() {
        return kelas;
    }

    public void setKelas(int kelas) {
        this.kelas = kelas;
    }

    public String getJurusan() {
        return jurusan;
    }

    public void setJurusan(String jurusan) {
        this.jurusan = jurusan;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }
}
