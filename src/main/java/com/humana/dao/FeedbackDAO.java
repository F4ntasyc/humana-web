package com.humana.dao;

import com.humana.model.Feedback;
import java.util.List;

/**
 * Interface DAO untuk entitas Feedback.
 * Mendefinisikan kontrak akses data untuk tabel feedback.
 */
public interface FeedbackDAO {

    Feedback findById(int id);

    Feedback findByPemesananId(int idPemesanan);

    List<Feedback> findByGuruId(int idGuru);

    List<Feedback> findAll();

    boolean insert(Feedback feedback);

    boolean delete(int id);
}
