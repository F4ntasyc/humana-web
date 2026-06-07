package com.humana.dao;

import com.humana.model.Pembayaran;
import java.util.List;

/**
 * Interface DAO untuk entitas Pembayaran.
 * Mendefinisikan kontrak akses data untuk tabel pembayaran.
 */
public interface PembayaranDAO {

    Pembayaran findById(int id);

    Pembayaran findByPemesananId(int idPemesanan);

    List<Pembayaran> findAll();

    boolean insert(Pembayaran pembayaran);

    boolean updateStatus(int idPembayaran, String statusBaru, String metodePembayaran);

    boolean delete(int id);
}
