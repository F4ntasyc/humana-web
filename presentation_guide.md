# Panduan Presentasi Kelompok: Aplikasi HUMANA (6 Orang)

Panduan ini disusun agar pembagian materi presentasi merata dan sesuai dengan format standar dosen (menjelaskan alur dari View/JSP, `doGet()`, `doPost()`, dan pemanggilan Model/DAO).

---

## 👨‍💻 Anggota 1: Fitur Otentikasi (Login, Register & Logout)
**Fokus File:** `AuthServlet.java`, `login.jsp`, `register.jsp`, `MuridDAO.java`, `GuruDAO.java`

* **Fitur yang Dikerjakan:** Proses pendaftaran pengguna baru (Murid/Guru) dan proses masuk (Login) serta keluar (Logout).
* **Alur Penjelasan (Sesuai Ketentuan):**
  1. **View (JSP):** Fitur ini dimulai saat pengguna membuka link `<a href="/humana-web/auth/login">` atau `<a href="/humana-web/auth/register">`.
  2. **`doGet()`:** `AuthServlet` pada method `doGet()` hanya bertugas menampilkan (forward) halaman `login.jsp` atau `register.jsp` kepada pengguna tanpa mengambil data kompleks.
  3. **Pengisian Form (JSP):** Pengguna mengisi form pendaftaran atau login, lalu form di-submit dengan `<form action="..." method="post">`.
  4. **`doPost()`:** Data form ditangkap oleh `doPost()` di `AuthServlet`.
  5. **Model / Method:** `doPost()` memanggil method di `MuridDAO` atau `GuruDAO` (contoh: `tambahMurid()` atau `verifikasiLogin()`). Jika berhasil (CRUD: Create/Read), sistem membuat sesi (Session) dan mengarahkan pengguna ke Dashboard.

---

## 👨‍💻 Anggota 2: Fitur Pengelolaan Profil
**Fokus File:** `ProfileServlet.java`, `profil.jsp`, `MuridDAO.java`, `GuruDAO.java`

* **Fitur yang Dikerjakan:** Menampilkan data diri pengguna (Murid/Guru), mengubah biodata, dan mengubah pengaturan privasi/password.
* **Alur Penjelasan (Sesuai Ketentuan):**
  1. **View (JSP):** Dipanggil melalui menu navigasi (sidebar/header) yang mengarah ke link `/profile`.
  2. **`doGet()`:** `ProfileServlet` pada method `doGet()` bertugas mengambil data profil dari database menggunakan `MuridDAO.getMuridById()` atau `GuruDAO.getGuruById()`. Data ini disiapkan (di-set sebagai atribut) lalu dikirim ke halaman `profil.jsp`. (CRUD: Read)
  3. **Pengisian Form (JSP):** Di halaman `profil.jsp`, data lama ditampilkan di dalam form. Pengguna mengubah data lalu submit form via method `POST`.
  4. **`doPost()`:** Data profil yang baru diterima oleh `doPost()` di `ProfileServlet`.
  5. **Model / Method:** Servlet memanggil method `updateMurid()` atau `updateGuru()` pada Model DAO untuk menyimpan perubahan ke database. (CRUD: Update)

---

## 👨‍💻 Anggota 3: Fitur Eksplorasi Materi (Dashboard & Daftar Materi)
**Fokus File:** `DashboardServlet.java`, `MateriServlet.java`, `dashboard.jsp`, `list.jsp` (materi), `detail.jsp` (materi)

* **Fitur yang Dikerjakan:** Halaman beranda utama murid (menampilkan rekomendasi materi) dan fitur untuk melihat daftar seluruh materi beserta detail dari materi tersebut. Di aplikasi ini murid murni mengeksplorasi "Materi", bukan mencari "Guru".
* **Alur Penjelasan (Sesuai Ketentuan):**
  1. **View (JSP):** Dipanggil otomatis setelah login sukses (ke URL `/dashboard`), atau saat mengklik menu navigasi yang mengarah ke daftar materi di URL `/materi`.
  2. **`doGet()`:** `DashboardServlet` menyiapkan data rekomendasi materi secara acak/berdasarkan jenjang murid. Sementara `MateriServlet` (pada method `doGet()`) bertugas menarik seluruh daftar materi dari database via join SQL ke tabel `MataPelajaran`, kemudian dikirimkan ke `dashboard.jsp` atau `list.jsp` untuk di-render. (CRUD: Read)
  3. **Interaksi (JSP):** Dari daftar materi tersebut, murid dapat mengklik kartu materi untuk masuk ke `/materi/detail?id=...`. `MateriServlet` kembali bertugas mengambil satu data detail materi spesifik dari DB dan merendernya di `detail.jsp`. Di halaman detail inilah terdapat tombol "Pesan Sesi" (menggunakan link `<a href="...">`) yang akan membawa murid ke alur pemesanan.
  *Note: Pada fitur ini, fokusnya sangat kuat di sisi Read (R pada CRUD).*

---

## 👨‍💻 Anggota 4: Fitur Pemesanan Sesi Belajar (Create Pesanan)
**Fokus File:** `PemesananServlet.java`, `pesan.jsp`, `PemesananDAO.java`

* **Fitur yang Dikerjakan:** Proses murid membuat "Pesanan Baru" untuk sesi belajar berdasarkan materi pelajaran (sistem *broadcast* pencarian guru), lengkap dengan penentuan tanggal, waktu, dan lokasi.
* **Alur Penjelasan (Sesuai Ketentuan):**
  1. **View (JSP):** Berawal dari klik tombol "Pesan Sesi" (biasanya diakses dari detail materi di fitur Anggota 3 atau langsung dari menu navigasi pesan), yang mengarah ke URL `/pesan`.
  2. **`doGet()`:** `PemesananServlet` pada method `doGet()` menarik daftar seluruh materi pelajaran dari database untuk ditampilkan sebagai pilihan *dropdown*, lalu mengirimkannya (forward) ke halaman form `pesan.jsp`.
  3. **Pengisian Form (JSP):** Murid memilih materi yang ingin dipelajari, mengisi jadwal (waktu mulai dan selesai), serta menentukan lokasi sesi, lalu men-*submit* form menggunakan method `POST`.
  4. **`doPost()`:** Seluruh data pesanan ditangkap oleh `doPost()` di `PemesananServlet`.
  5. **Model / Method:** `doPost()` mengeksekusi query insert (atau memanggil Model/DAO) untuk menyimpan data ke dalam tabel `Pemesanan` dengan status awal "menunggu konfirmasi". Pesanan ini dibuat tanpa ID guru (*null*), karena sistem bersifat *broadcast* agar nantinya bisa diambil oleh guru mana saja yang mengajar materi tersebut. (CRUD: Create)

---

## 👨‍💻 Anggota 5: Fitur Jadwal Aktif & Pembayaran
**Fokus File:** `HistoryServlet.java` (bagian Jadwal), `PembayaranServlet.java`, `jadwal-aktif.jsp`, `bayar.jsp`, `PembayaranDAO.java`

* **Fitur yang Dikerjakan:** Menampilkan jadwal yang sedang berjalan, konfirmasi sesi oleh Guru (Terima/Tolak), dan proses pembayaran oleh Murid.
* **Alur Penjelasan (Sesuai Ketentuan):**
  1. **View (JSP):** Halaman dibuka dari link menu "Jadwal Saya".
  2. **`doGet()`:** `HistoryServlet` (`doGet()`) menyiapkan daftar pesanan berstatus aktif (memanggil DB) untuk dikirim ke `jadwal-aktif.jsp` (CRUD: Read). Jika murid klik "Bayar", akan memanggil `doGet()` di `PembayaranServlet` untuk menyiapkan tagihan dan me-render `bayar.jsp`.
  3. **Pengisian Form (JSP):** Guru menekan tombol Terima/Tolak (submit POST form konfirmasi) ATAU Murid mengunggah bukti bayar di form `bayar.jsp` (method POST).
  4. **`doPost()`:** Request diterima oleh `doPost()` di servlet masing-masing.
  5. **Model / Method:** `PembayaranServlet` memanggil `simpanPembayaran()` di DAO untuk menyimpan data bayar (Create). `HistoryServlet` memanggil method update di Model (atau eksekusi query Update) untuk mengubah status pesanan menjadi "Dikonfirmasi" atau "Batal" (CRUD: Update).

---

## 👨‍💻 Anggota 6: Fitur Riwayat, Ulasan (Feedback) & Pendapatan Guru
**Fokus File:** `HistoryServlet.java` (bagian Histori), `PendapatanServlet.java`, `histori.jsp`, `pendapatan.jsp`, Model Feedback/Pendapatan.

* **Fitur yang Dikerjakan:** Menampilkan riwayat sesi yang sudah selesai, murid memberikan rating bintang/ulasan, serta melihat saldo/pendapatan guru.
* **Alur Penjelasan (Sesuai Ketentuan):**
  1. **View (JSP):** Diakses via menu "Riwayat Sesi" atau menu "Pendapatan" untuk guru.
  2. **`doGet()`:** `HistoryServlet` atau `PendapatanServlet` (`doGet()`) menyiapkan data riwayat sesi (status "Selesai") atau data rekapitulasi pembayaran (CRUD: Read). Data dikirim untuk ditampilkan di `histori.jsp`.
  3. **Pengisian Form (JSP):** Pada `histori.jsp`, murid dapat mengklik "Beri Ulasan", yang memunculkan pop-up form (modal) berisi rating bintang dan komentar, lalu disubmit (method POST).
  4. **`doPost()`:** `HistoryServlet` (`doPost()` bagian `/histori/feedback`) menangkap data rating dan komentar.
  5. **Model / Method:** `doPost()` menggunakan query/Model untuk memasukkan data ke tabel Feedback (CRUD: Create), lalu memanggil method untuk Update rata-rata rating pada tabel Guru (CRUD: Update).

---

### 💡 Tips Tambahan untuk Semuanya Saat Presentasi:
- **Tunjukkan langsung halamannya (JSP).** Mulai presentasi dengan simulasi/demo klik fitur tersebut.
- Ucapkan kalimat seperti: *"Halaman ini adalah `profil.jsp`. Halaman ini dipanggil bukan langsung dari file JSP, melainkan dari controller `ProfileServlet` bagian `doGet()` yang bertugas menyiapkan data..."*
- Tunjukkan kodenya di IDE (NetBeans/IntelliJ/VSCode): *"Setelah form disubmit, ia masuk ke method `doPost()`. Di sini, kita memanggil `MuridDAO.updateMurid(data)` untuk berinteraksi dengan database..."*
