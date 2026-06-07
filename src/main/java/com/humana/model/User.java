package com.humana.model;

/**
 * Abstract class User — superclass untuk semua jenis pengguna (Guru, Murid, Admin).
 * Memiliki abstract method {@link #getRole()} yang wajib diimplementasikan oleh subclass.
 *
 * <p>Adaptasi dari: backend/src/classes/User.js</p>
 * <p>Perbaikan OOP:
 * <ul>
 *   <li>Semua field menggunakan private access + getter/setter</li>
 *   <li>getRole() mengembalikan String (sesuai konvensi project)</li>
 *   <li>Method login() tetap dipertahankan sebagai validasi sederhana</li>
 * </ul>
 * </p>
 */
public abstract class User {

    private int id;
    private String username;
    private String email;
    private String password;
    private String namaUser;

    /**
     * Constructor default (diperlukan untuk instansiasi dari DAO/ResultSet).
     */
    public User() {
    }

    /**
     * Constructor lengkap.
     */
    public User(int id, String username, String email, String password, String namaUser) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.namaUser = namaUser;
    }

    /**
     * Constructor tanpa id (untuk insert baru — id auto increment dari DB).
     */
    public User(String username, String email, String password, String namaUser) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.namaUser = namaUser;
    }

    // ======== Abstract Method ========

    /**
     * Mengembalikan role pengguna. Wajib diimplementasikan oleh setiap subclass.
     *
     * @return String role: "GURU", "MURID", atau "ADMIN"
     */
    public abstract String getRole();

    // ======== Business Methods ========

    /**
     * Validasi login sederhana — mencocokkan email dan password.
     * Adaptasi dari User.js login().
     *
     * @param inputEmail    email yang diinput
     * @param inputPassword password yang diinput
     * @return true jika cocok
     */
    public boolean login(String inputEmail, String inputPassword) {
        if (inputEmail == null || inputPassword == null) return false;
        return this.email.trim().equals(inputEmail.trim())
            && this.password.trim().equals(inputPassword.trim());
    }

    // ======== Getters & Setters ========

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNamaUser() {
        return namaUser;
    }

    public void setNamaUser(String namaUser) {
        this.namaUser = namaUser;
    }
}
