# Audit HUMANA Web — Ringkasan Sebelum PR ke Main

Dokumen ini merangkum hasil pengecekan alur **Murid** dan **Guru** pada proyek `humana-web` (Servlet + JSP + MariaDB), dibandingkan dengan aplikasi mobile HUMANA (React Native + Node.js).

---

## Ringkasan Eksekutif

Alur inti web **sudah bisa didemo end-to-end** untuk kebutuhan tugas kuliah: daftar → login → pesan/terima → bayar → jadwal → selesai → histori → feedback → profil. Perbaikan UI terakhir (profil, dashboard guru, menunggu guru, pesan sesi) sudah konsisten.

Web **belum setara penuh dengan mobile** — chat, notifikasi, Midtrans, GPS, dan batal sesi kompleks belum diimplementasi. Beberapa celah logika dan keamanan perlu diakui secara jujur saat demo.

---

## POV Murid — Siap Dipresentasikan

| Alur | Status |
|------|--------|
| Registrasi & login | Berfungsi, redirect ke dashboard |
| Dashboard | Statistik, jadwal terdekat, rekomendasi materi per jenjang |
| Pesan sesi | Validasi durasi min. 1 jam, estimasi biaya, pencarian materi |
| Menunggu guru | Auto-refresh, redirect ke bayar jika dikonfirmasi, batalkan permintaan |
| Jadwal aktif | Tab menunggu/aktif, bayar, batalkan, badge status |
| Pembayaran | Simulasi bayar → status `lunas` (cukup untuk demo) |
| Histori & feedback | Rating 1–5, update rating guru |
| Profil | Data dasar + jenjang/kelas dengan validasi |

**Happy path demo:** Murid pesan → guru terima → murid bayar → sesi berlangsung → guru selesai → murid kasih ulasan.

---

## POV Guru — Siap Dipresentasikan

| Alur | Status |
|------|--------|
| Registrasi & login | Default nonaktif (`is_active = 0`) — perlu diaktifkan di profil |
| Dashboard | Statistik, permintaan masuk, shortcut navigasi |
| Terima permintaan | Filter materi + guru aktif, race condition ditangani |
| Batalkan (pre-bayar) | Guru lepas sesi → kembali `menunggu konfirmasi` |
| Selesai sesi | Hanya setelah pembayaran `lunas` |
| Jadwal aktif | Tab permintaan vs aktif, aksi sesuai status |
| Profil | Ketersediaan, kelola materi + pencarian, portfolio |
| Pendapatan | Total, bulan ini, riwayat sesi lunas |
| Histori | Sesi selesai/dibatalkan + rating |

**Happy path demo:** Guru aktifkan ketersediaan + pilih materi → terima permintaan → tunggu murid bayar → selesaikan sesi → cek pendapatan & histori.

---

## Masih Perlu Diperbaiki — Prioritas Tinggi (Logika)

### Berlaku untuk Murid & Guru

1. **Password plain text** — login membandingkan password langsung tanpa hashing. Sama dengan mobile, tetapi tetap risiko keamanan jika diklaim production-ready.
2. **Endpoint JSON tanpa session** — `/matching/*` dan `/pendapatan/{id}` bisa dipanggil tanpa login. UI web memakai servlet yang aman (`HistoryServlet`), tetapi endpoint legacy tetap terbuka.
3. **Tidak ada CSRF token** — semua form POST rentan (umum untuk tugas kuliah, tetapi perlu disebutkan).

### Khusus Murid

4. **Biaya transport selalu Rp 0** — lokasi web berupa textarea teks, bukan koordinat GPS seperti mobile. Saat guru terima via web, biaya jarak tidak dihitung.
5. **Murid hanya bisa batalkan saat `menunggu konfirmasi`** — tidak ada refund/penalty pasca-bayar seperti di mobile.
6. **Histori tidak menampilkan `dibatalkan_murid` / `dibatalkan_guru`** — hanya `selesai` dan `dibatalkan`. Sesi dari mobile bisa tidak muncul di histori web.
7. **Halaman bayar statis** — tidak ada polling jika guru membatalkan setelah terima; murid tidak otomatis tahu.
8. **Tidak cek profil lengkap sebelum pesan** — murid baru (`kelas = 0`) tetap bisa melakukan booking.
9. **Bug tampilan dashboard** — nama guru saat menunggu bisa menampilkan string HTML mentah, bukan teks "Menunggu Konfirmasi" (`murid/dashboard.jsp`).

### Khusus Guru

10. **Biaya jarak = 0 saat terima permintaan** — berdampak pada nominal tagihan murid.
11. **Rating bisa berbeda** — dashboard memakai kolom `Guru.rating`, profil memakai `AVG(Feedback)`.
12. **Shortcut "Materi" di dashboard** mengarah ke `/materi` (katalog), bukan tab kelola materi di profil.
13. **Guru baru tidak diarahkan** — setelah daftar harus manual mengaktifkan ketersediaan dan memilih materi (tidak ada onboarding).

---

## Masih Perlu Diperbaiki — Prioritas Sedang (UI/UX)

| Isu | POV |
|-----|-----|
| Ikon notifikasi di header tidak berfungsi | Keduanya |
| Login: "Lupa sandi", Google, "Ingat saya" — UI saja | Keduanya |
| Form pesan: semua materi ditampilkan tanpa filter jenjang/kelas murid | Murid |
| Halaman menunggu: tanpa peta/radar seperti mobile | Murid |
| Lokasi di kartu jadwal tampil mentah (`lat,lng\|alamat`) jika format mobile | Guru |
| Tidak ada halaman detail sesi/permintaan (peta, breakdown biaya) | Guru |
| Histori guru: komentar feedback tidak ditampilkan | Guru |
| Layout belum mobile-responsive (sidebar fixed) | Keduanya |
| Form registrasi panjang bisa terpotong di layar kecil | Keduanya |

---

## Fitur Mobile yang Belum Ada di Web

| Fitur | Murid | Guru |
|-------|:-----:|:----:|
| Chat | ❌ | ❌ |
| Notifikasi in-app | ❌ | ❌ |
| Midtrans / payment gateway | ❌ (simulasi klik) | — |
| GPS / map picker lokasi | ❌ | ❌ |
| Filter materi bertingkat (jenjang → mapel → materi) | ❌ | — |
| Draft pemesanan | ❌ (DAO ada, belum terhubung UI) | — |
| Batal sesi kompleks (refund, penalty 2 jam) | ❌ | ❌ |
| Foto dokumentasi saat selesai sesi | — | ❌ |
| Filter pendapatan per bulan/tahun | — | ❌ |
| Timeout gagal cari guru | ❌ | — |

---

## Rangkuman

### Yang sudah aman untuk didemo

- Seluruh happy path Murid dan Guru (pesan → terima → bayar → jadwal → selesai → histori → feedback → profil)
- Alur pembayaran: dibuat saat guru terima, bukan saat booking
- Validasi guru aktif, materi sesuai, dan selesai sesi hanya setelah lunas
- UI profil, dashboard guru, halaman menunggu guru, dan kelola materi

### Keterbatasan yang perlu disebutkan saat demo

- Pembayaran simulasi, bukan Midtrans
- Lokasi manual → biaya transport Rp 0
- Chat dan notifikasi belum ada di web
- Password plain text (disengaja untuk tugas, sama seperti mobile)

### Perbaikan cepat yang disarankan sebelum PR

1. Perbaiki bug tampilan nama guru di dashboard murid (`c:choose` menggantikan ternary HTML)
2. Arahkan shortcut "Materi" guru ke `/profil?tab=materi`
3. Tambahkan status `dibatalkan_murid` / `dibatalkan_guru` di query histori

### Bisa ditunda pasca-PR

- GPS + perhitungan biaya jarak
- Pengamanan endpoint `/matching/*`
- Chat, notifikasi, dan integrasi Midtrans

---

*Terakhir diperbarui: Juni 2025 — branch `stresstest`*
