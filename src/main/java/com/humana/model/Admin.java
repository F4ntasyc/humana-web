package com.humana.model;

/**
 * Model class Admin — merepresentasikan pengguna admin sistem.
 * Extends {@link User}.
 *
 * <p>Adaptasi dari: backend/src/classes/Admin.js</p>
 * <p>Perbaikan OOP:
 * <ul>
 *   <li>Semua business logic (tambahMateri, editMateri, hapusMateri, getAllMateri, getAllUser)
 *       dipindahkan ke servlet/service layer</li>
 *   <li>Admin hanya sebagai POJO murni — tidak punya tabel DB khusus
 *       (di-hardcode di session sesuai panduan proyek)</li>
 * </ul>
 * </p>
 */
public class Admin extends User {

    // ======== Constructors ========

    public Admin() {
        super();
    }

    /**
     * Constructor lengkap.
     */
    public Admin(int id, String username, String email, String password, String namaUser) {
        super(id, username, email, password, namaUser);
    }

    /**
     * Constructor tanpa id.
     */
    public Admin(String username, String email, String password, String namaUser) {
        super(username, email, password, namaUser);
    }

    // ======== Abstract Implementation ========

    @Override
    public String getRole() {
        return "ADMIN";
    }
}
