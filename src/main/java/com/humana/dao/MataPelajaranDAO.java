package com.humana.dao;

import com.humana.model.MataPelajaran;
import java.util.List;

/**
 * Interface DAO untuk entitas MataPelajaran.
 * Mendefinisikan kontrak akses data untuk tabel mata_pelajaran.
 */
public interface MataPelajaranDAO {

    MataPelajaran findById(int id);

    List<MataPelajaran> findAll();

    List<MataPelajaran> findByJenjang(String jenjang);

    boolean insert(MataPelajaran mataPelajaran);

    boolean update(MataPelajaran mataPelajaran);

    boolean delete(int id);
}
