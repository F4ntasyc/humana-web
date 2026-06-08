package com.humana.model;

/**
 * Interface Notifiable — kontrak untuk entitas yang dapat menerima notifikasi.
 * Diimplementasikan oleh {@link Guru} dan {@link Murid}.
 *
 * <p>Adaptasi dari: backend/src/classes/Notifiable.js</p>
 */
public interface Notifiable {

    /**
     * Menerima dan memproses notifikasi.
     *
     * @param message isi pesan notifikasi
     */
    void receiveNotification(String message);
}
