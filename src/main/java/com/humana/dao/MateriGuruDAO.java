package com.humana.dao;

import com.humana.model.Guru;
import com.humana.model.Materi;
import java.util.List;

/**
 * Interface DAO untuk tabel relasi MateriGuru (Many-to-Many).
 * Menghubungkan guru dengan materi yang mereka ampu.
 *
 * <p>Tabel DB: materi_guru (id_guru PK FK, id_materi PK FK)</p>
 */
public interface MateriGuruDAO {

    /**
     * Mendapatkan semua materi yang diampu oleh guru tertentu.
     * @param idGuru id guru
     * @return list materi
     */
    List<Materi> findMateriByGuruId(int idGuru);

    /**
     * Mendapatkan semua guru yang mengampu materi tertentu.
     * @param idMateri id materi
     * @return list guru
     */
    List<Guru> findGuruByMateriId(int idMateri);

    /**
     * Menambahkan relasi guru-materi.
     * @param idGuru id guru
     * @param idMateri id materi
     * @return true jika berhasil
     */
    boolean insert(int idGuru, int idMateri);

    /**
     * Menghapus relasi guru-materi.
     * @param idGuru id guru
     * @param idMateri id materi
     * @return true jika berhasil
     */
    boolean delete(int idGuru, int idMateri);
}
