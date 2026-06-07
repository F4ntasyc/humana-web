package com.humana.model;

/**
 * Model class Murid — merepresentasikan data murid/siswa.
 * Extends {@link User} dan implements {@link Notifiable}.
 *
 * <p>Adaptasi dari: backend/src/classes/Murid.js</p>
 * <p>Perbaikan OOP:
 * <ul>
 *   <li>Field saldo dan orderHistory dihapus — tidak ada di skema DB</li>
 *   <li>Business logic (pesanSesi, batalkanPemesanan, beriFeedback) dipindahkan ke servlet</li>
 *   <li>Utility method getJenjang() dan getKelasJurusan() tetap dipertahankan di model</li>
 *   <li>jenisKelamin disimpan sebagai String ("L"/"P") sesuai DB</li>
 * </ul>
 * </p>
 *
 * <p>Kolom DB: id_murid, nama_murid, email, username, password,
 * no_telepon, jenis_kelamin, alamat, kelas, jurusan</p>
 */
public class Murid extends User implements Notifiable {

    private String noTelepon;
    private String jenisKelamin; // "L" atau "P"
    private String alamat;
    private int kelas;
    private String jurusan;

    // ======== Constructors ========

    public Murid() {
        super();
    }

    /**
     * Constructor lengkap (untuk mapping dari ResultSet DB).
     */
    public Murid(int id, String username, String email, String password, String namaUser,
                 String noTelepon, String jenisKelamin, String alamat,
                 int kelas, String jurusan) {
        super(id, username, email, password, namaUser);
        this.noTelepon = noTelepon;
        this.jenisKelamin = jenisKelamin;
        this.alamat = alamat;
        this.kelas = kelas;
        this.jurusan = jurusan;
    }

    /**
     * Constructor untuk registrasi baru (tanpa id).
     */
    public Murid(String username, String email, String password, String namaUser,
                 String noTelepon, String jenisKelamin, String alamat,
                 int kelas, String jurusan) {
        super(username, email, password, namaUser);
        this.noTelepon = noTelepon;
        this.jenisKelamin = jenisKelamin;
        this.alamat = alamat;
        this.kelas = kelas;
        this.jurusan = jurusan;
    }

    // ======== Abstract & Interface Implementations ========

    @Override
    public String getRole() {
        return "MURID";
    }

    @Override
    public void receiveNotification(String message) {
        System.out.println("Notifikasi Murid [" + getNamaUser() + "]: " + message);
    }

    // ======== Utility Methods ========

    /**
     * Menentukan jenjang pendidikan berdasarkan nomor kelas.
     * Kelas 1-6 → SD, 7-9 → SMP, 10-12 → SMA.
     * Adaptasi langsung dari Murid.js getJenjang().
     *
     * @return String jenjang pendidikan, atau "-" jika tidak valid
     */
    public String getJenjang() {
        if (kelas >= 1 && kelas <= 6) return "SD";
        if (kelas >= 7 && kelas <= 9) return "SMP";
        if (kelas >= 10 && kelas <= 12) return "SMA";
        return "-";
    }

    /**
     * Mendapatkan label kelas dan jurusan digabung.
     * Contoh: "11 - IPA", "7", atau "-".
     * Adaptasi dari Murid.js getKelasJurusan().
     */
    public String getKelasJurusan() {
        String jurusanClean = (jurusan != null && !jurusan.isEmpty() && !"(NULL)".equals(jurusan))
                ? jurusan : "-";

        if (kelas > 0 && !"-".equals(jurusanClean)) {
            return kelas + " - " + jurusanClean;
        }
        return kelas > 0 ? String.valueOf(kelas) : "-";
    }

    /**
     * Mendapatkan label jenis kelamin lengkap.
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
}
