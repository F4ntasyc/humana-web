package com.humana.dao;

import com.humana.model.Murid;
import java.util.List;

/**
 * Interface DAO untuk entitas Murid.
 * Mendefinisikan kontrak akses data untuk tabel murid.
 */
public interface MuridDAO {

    Murid findById(int id);

    Murid findByEmail(String email);

    Murid findByUsername(String username);

    List<Murid> findAll();

    boolean insert(Murid murid);

    boolean update(Murid murid);

    boolean delete(int id);
}
