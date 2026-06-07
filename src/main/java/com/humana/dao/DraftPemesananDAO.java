package com.humana.dao;

import com.humana.model.DraftPemesanan;

/**
 * Interface DAO untuk entitas DraftPemesanan.
 * Mendefinisikan kontrak akses data untuk tabel draft_pemesanan.
 *
 * <p>Tabel ini menggunakan id_murid sebagai PK (satu murid = satu draft aktif).</p>
 */
public interface DraftPemesananDAO {

    /**
     * Mencari draft berdasarkan ID murid.
     * @param idMurid id murid
     * @return objek DraftPemesanan, atau null jika tidak ada draft
     */
    DraftPemesanan findByMuridId(int idMurid);

    /**
     * Menyimpan atau mengupdate draft.
     * Jika draft untuk murid tersebut sudah ada, maka di-update.
     * Jika belum ada, maka di-insert.
     *
     * @param draft objek DraftPemesanan
     * @return true jika berhasil
     */
    boolean insertOrUpdate(DraftPemesanan draft);

    /**
     * Menghapus draft berdasarkan ID murid.
     * @param idMurid id murid
     * @return true jika berhasil
     */
    boolean delete(int idMurid);
}
