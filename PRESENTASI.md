# Panduan Naskah Presentasi Video - HUMANA Web

Dokumen ini berisi pembagian materi beserta **teks/naskah panduan** untuk video presentasi tugas besar PBO. Karena kita masih awam, setiap poin sudah dilengkapi dengan penjelasan sederhana (bisa dibaca atau dihafal).

---

## 👤 Orang 1: Pendahuluan & Latar Belakang
**Tugas:** Membuka presentasi dan menjelaskan gambaran umum proyek.

*   **Pembukaan & Latar Belakang:** 
    *   *Yang harus diucapkan/dijelaskan:* "Halo semuanya, kami dari kelompok [Nama Kelompok] akan mempresentasikan tugas besar PBO kami yaitu aplikasi 'HUMANA Web'. Aplikasi ini adalah platform les privat. Latar belakang kami membuat ini karena saat ini banyak murid kesulitan mencari guru les yang pas, dan sebaliknya, guru juga butuh wadah untuk mencari murid. Jadi, kami membuat aplikasi untuk mempertemukan mereka."
*   **Target Pengguna:** 
    *   *Yang harus diucapkan/dijelaskan:* "Target pengguna utama web kami ada dua, yaitu 'Murid' yang ingin mencari materi atau guru les, dan 'Guru' yang akan menyediakan jasa les."
*   **Tech Stack (Teknologi):** 
    *   *Yang harus diucapkan/dijelaskan:* "Untuk membuatnya, kami menggunakan bahasa Java (Java 17). Di bagian *backend* kami pakai Servlet dan JSP, lalu databasenya menggunakan MariaDB. Untuk tampilan antarmuka (UI), kami memakai Bootstrap 5 agar webnya rapi dan responsif."
*   **Apa itu MVC? (Struktur Proyek):** 
    *   *Yang harus diucapkan/dijelaskan:* "Aplikasi kami dibangun dengan konsep **MVC (Model-View-Controller)**. Singkatnya, MVC memisahkan kode jadi 3 bagian: 
        *   **Model**: Bagian yang ngurusin data dan nyambung ke database. 
        *   **View**: Tampilan halaman web yang dilihat oleh *user*.
        *   **Controller**: Otak aplikasinya, yang memproses klik/input dari *user* lalu memberikan tampilan yang sesuai. Nah, masing-masing bagian ini akan dijelaskan secara detail oleh teman-teman saya."

---

## 👤 Orang 2: Arsitektur Database & Model (Bagian "Model")
**Tugas:** Menjelaskan bagian database dan OOP Model.

*   **Skema Database (Tampilkan Gambar ERD/Tabel):** 
    *   *Yang harus diucapkan/dijelaskan:* "Melanjutkan penjelasan tentang MVC tadi, saya akan bahas bagian **Model** atau datanya. Ini adalah bentuk database kami. Kami pakai MariaDB. Ada beberapa tabel penting, seperti tabel Guru, tabel Murid, tabel Pesanan (untuk mencatat pesanan les), dan tabel Pembayaran."
*   **Koneksi Database (DBConnection):** 
    *   *Yang harus diucapkan/dijelaskan:* "Agar kode Java kita bisa ngobrol dengan database MariaDB, kami membuat sistem `DBConnection`. Ini tugasnya ibarat 'kabel penghubung' supaya aplikasi web bisa narik atau nambah data ke database."
*   **Class Model (OOP):** 
    *   *Yang harus diucapkan/dijelaskan:* "Karena ini mata kuliah OOP (Pemrograman Berorientasi Objek), setiap tabel di database tadi kami ubah jadi 'Objek' di Java. Misalnya, tabel Guru diubah jadi `class Guru`, tabel Murid jadi `class Murid`. Di dalam *class* ini isinya variabel seperti nama, email, beserta fungsi *getter* dan *setter* untuk mengambil dan mengubah data tersebut."

---

## 👤 Orang 3: DAO (Data Access Object) - Data Master
**Tugas:** Menjelaskan bagaimana kode Java mengambil data dari database.

*   **Apa itu DAO?** 
    *   *Yang harus diucapkan/dijelaskan:* "Selanjutnya saya akan menjelaskan tentang **DAO (Data Access Object)**. Simpelnya, DAO ini adalah file khusus di Java yang isinya perintah-perintah SQL. Kenapa dipisah di DAO? Agar kodenya rapi. Jadi kalau mau nyari atau nambah data, kita cukup panggil DAO ini, nggak usah nulis perintah SQL kepanjangan di tempat lain."
*   **CRUD Data Pengguna:** 
    *   *Yang harus diucapkan/dijelaskan:* "Di proyek ini, kami punya `GuruDAO` dan `MuridDAO`. DAO ini punya fitur CRUD (Create, Read, Update, Delete). Contohnya, saat ada user baru daftar, `GuruDAO` atau `MuridDAO` akan menjalankan perintah SQL `INSERT INTO...` untuk memasukkan data akun mereka ke database."
*   **Contoh Implementasi:** 
    *   *Yang harus diucapkan/dijelaskan:* "Begitu juga saat user mencoba login, DAO akan menjalankan perintah SQL `SELECT` untuk ngecek apakah email dan password yang dimasukkan cocok dengan yang ada di database."

---

## 👤 Orang 4: DAO - Transaksi Bisnis
**Tugas:** Menjelaskan aliran data untuk fitur booking dan bayar.

*   **Alur Pemesanan (PemesananDAO):** 
    *   *Yang harus diucapkan/dijelaskan:* "Selain data pengguna, kami juga punya DAO untuk fitur utama, yaitu memesan guru, namanya `PemesananDAO`. Saat murid klik tombol 'Booking', DAO ini akan mengirim data seperti: murid siapa, guru siapa, dan pesannya apa, untuk disimpan ke tabel pesanan di database."
*   **Alur Pembayaran (PembayaranDAO):** 
    *   *Yang harus diucapkan/dijelaskan:* "Lalu ada `PembayaranDAO`. Saat murid melakukan pembayaran, DAO ini bertugas mengubah 'status pembayaran' di database dari 'Belum Lunas' menjadi 'Lunas' atau 'Menunggu Konfirmasi'."
*   **Menggabungkan Data (Query JOIN):** 
    *   *Yang harus diucapkan/dijelaskan:* "Untuk menampilkan riwayat atau histori pesanan secara lengkap, kami menggunakan query SQL `JOIN` di dalam DAO. Ini berfungsi untuk menggabungkan data nama dari tabel Murid dan tabel Guru, jadi satu tampilan tabel yang utuh untuk dibaca."

---

## 👤 Orang 5: Controller (Bagian Pengatur / Servlet)
**Tugas:** Menjelaskan otak aplikasi (Servlet) yang mengatur klik dari user.

*   **Peran Controller (Servlet):** 
    *   *Yang harus diucapkan/dijelaskan:* "Kembali lagi ke konsep MVC, saya akan menjelaskan bagian **Controller**, yang di Java disebut **Servlet**. Servlet ini ibarat 'pelayan restoran'. Saat *user* nge-klik tombol di website, Servlet yang akan menerima perintah itu, lalu minta data ke DAO, dan ngasih hasil akhirnya ke tampilan web."
*   **Fitur Autentikasi (Login/Register):** 
    *   *Yang harus diucapkan/dijelaskan:* "Contohnya di fitur Login. Ada file namanya `LoginServlet`. Saat *user* isi form login dan klik submit, `LoginServlet` akan nangkap email dan password-nya, nanya ke DAO apakah datanya benar. Kalau benar, Servlet ini akan mengizinkan user masuk ke halaman utama webnya."
*   **Fitur Profil & Materi:** 
    *   *Yang harus diucapkan/dijelaskan:* "Begitu juga dengan `ProfileServlet` atau bagian materi. Servlet ini bertugas ngambil data profil terbaru dari database lewat DAO, lalu mengirim data itu ke halaman profil supaya bisa dilihat oleh user."

---

## 👤 Orang 6: Fitur Transaksi, View (UI), & Penutup
**Tugas:** Menjelaskan tampilan (View), sisa fitur, lalu menutup presentasi.

*   **Fitur Transaksi:** 
    *   *Yang harus diucapkan/dijelaskan:* "Untuk fitur transaksinya, kami punya Servlet untuk Pemesanan dan Pembayaran. Saat user bayar, Servlet ini yang akan nangkap nominal atau bukti bayar dan memanggil DAO untuk mencatatnya ke database."
*   **Bagian View (Tampilan Antarmuka):** 
    *   *Yang harus diucapkan/dijelaskan:* "Terakhir dari konsep MVC adalah **View** atau tampilannya. Kami membuat tampilan web ini menggunakan **JSP (JavaServer Pages)**. Bentuknya seperti HTML, tapi JSP ini bisa menerima data dari Java. Jadi tulisan seperti nama user yang login bisa dinamis muncul di halaman."
*   **Desain UI (Tampilkan Layar/Demo Web):** 
    *   *Yang harus diucapkan/dijelaskan:* "Agar webnya tidak terlihat kaku, kami mendesain UI-nya menggunakan framework **Bootstrap 5**. Jadi warna, tombol, dan navigasinya sudah rapi dan kekinian. [Kalian bisa tampilkan demo / scroll layar webnya sebentar di sini]."
*   **Penutup:** 
    *   *Yang harus diucapkan/dijelaskan:* "Kesimpulannya, aplikasi HUMANA Web ini berhasil kami buat menggunakan Java dengan konsep MVC, di mana *Model* mengurus data, *Controller* atau Servlet mengatur logika, dan *View* atau JSP untuk antarmuka. Sekian presentasi dari kelompok kami. Terima kasih atas perhatiannya!"
