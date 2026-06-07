package com.humana.dao;

import com.humana.model.Pemesanan;
import java.util.List;

/**
 * Interface DAO untuk entitas Pemesanan.
 * Mendefinisikan kontrak akses data untuk tabel pemesanan.
 */
public interface PemesananDAO {

    Pemesanan findById(int id);

    List<Pemesanan> findByMuridId(int idMurid);

    List<Pemesanan> findByGuruId(int idGuru);

    List<Pemesanan> findByStatus(String statusPemesanan);

    List<Pemesanan> findAll();

    boolean insert(Pemesanan pemesanan);

    boolean updateStatus(int idPemesanan, String statusBaru);

    boolean update(Pemesanan pemesanan);

    boolean delete(int id);
}
