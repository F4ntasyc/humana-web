package com.humana.util;

import com.humana.model.Pemesanan;

import java.time.LocalDateTime;

/**
 * Kalkulasi biaya sesi — selaras dengan PemesananSesi.js / model Pemesanan.
 * Tarif: Rp 30.000/jam (minimum 1 jam), transport Rp 3.000/km (dibulatkan ke kelipatan 500).
 */
public final class BiayaUtil {

    private BiayaUtil() {}

    public static int[] hitungBiaya(LocalDateTime waktuMulai, LocalDateTime waktuSelesai, double jarakKm) {
        Pemesanan p = new Pemesanan();
        p.setWaktuMulai(waktuMulai);
        p.setWaktuSelesai(waktuSelesai);
        return p.hitungTotalBiaya(jarakKm);
    }

    public static int[] hitungBiayaTanpaJarak(LocalDateTime waktuMulai, LocalDateTime waktuSelesai) {
        return hitungBiaya(waktuMulai, waktuSelesai, 0);
    }
}
