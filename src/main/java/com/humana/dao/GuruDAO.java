package com.humana.dao;

import com.humana.model.Guru;
import java.util.List;

/**
 * Interface DAO untuk entitas Guru.
 * Mendefinisikan kontrak akses data untuk tabel guru.
 */
public interface GuruDAO {

    /**
     * Mencari guru berdasarkan ID.
     * @param id id_guru
     * @return objek Guru, atau null jika tidak ditemukan
     */
    Guru findById(int id);

    /**
     * Mencari guru berdasarkan email.
     * @param email email_guru (unique)
     * @return objek Guru, atau null jika tidak ditemukan
     */
    Guru findByEmail(String email);

    /**
     * Mencari guru berdasarkan username.
     * @param username username (unique)
     * @return objek Guru, atau null jika tidak ditemukan
     */
    Guru findByUsername(String username);

    /**
     * Mengambil semua data guru.
     * @return list semua guru
     */
    List<Guru> findAll();

    /**
     * Menyimpan guru baru ke database.
     * @param guru objek Guru yang akan disimpan
     * @return true jika berhasil
     */
    boolean insert(Guru guru);

    /**
     * Mengupdate data guru yang sudah ada.
     * @param guru objek Guru dengan data terbaru
     * @return true jika berhasil
     */
    boolean update(Guru guru);

    /**
     * Mengupdate rating guru.
     * @param idGuru id guru
     * @param rating rating baru
     * @return true jika berhasil
     */
    boolean updateRating(int idGuru, double rating);

    /**
     * Menghapus guru berdasarkan ID.
     * @param id id_guru
     * @return true jika berhasil
     */
    boolean delete(int id);
}
