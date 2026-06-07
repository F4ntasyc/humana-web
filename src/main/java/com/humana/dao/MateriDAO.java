package com.humana.dao;

import com.humana.model.Materi;
import java.util.List;

/**
 * Interface DAO untuk entitas Materi.
 * Mendefinisikan kontrak akses data untuk tabel materi.
 */
public interface MateriDAO {

    Materi findById(int id);

    List<Materi> findAll();

    List<Materi> findByMapelId(int idMapel);

    List<Materi> findByKelasAndJurusan(int kelas, String jurusan);

    boolean insert(Materi materi);

    boolean update(Materi materi);

    boolean delete(int id);
}
