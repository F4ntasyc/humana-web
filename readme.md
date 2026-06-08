# PANDUAN TIM — HUMANA Web Project

Panduan ini untuk anggota tim yang ingin join dan menjalankan project HUMANA di lokal masing-masing.

---

## BAGIAN 1 — Yang Kamu Lakukan Dulu (Sebelum Teman Join)

### 1.1 Buat .gitignore
    
Buat file `.gitignore` di root project (`humana-web/`), isi dengan:

```
# Maven
target/

# Database credential (JANGAN di-commit!)
src/main/resources/db.properties

# IDE
.idea/
*.iml
.vscode/settings.json
.classpath
.project
.settings/

# OS
.DS_Store
Thumbs.db
```

### 1.2 Buat file db.properties.example

Buat file `src/main/resources/db.properties.example` (ini boleh di-commit, isinya template kosong):

```properties
# Salin file ini menjadi db.properties lalu isi dengan credential kamu
db.url=jdbc:mariadb://HOST:PORT/humana-dev
db.username=USERNAME_DISINI
db.password=PASSWORD_DISINI
```

### 1.3 Buat README.md

Buat file `README.md` di root project, isi dengan:

```markdown
# HUMANA Web — Platform Les Privat

Aplikasi web platform les privat berbasis Java Servlet + JSP + Maven + MariaDB.

## Tech Stack
- Java 17
- Apache Maven 3.9+
- Apache Tomcat 10
- MariaDB (hosted)
- Servlet + JSP + JSTL + Bootstrap 5

## Setup Lokal

Ikuti panduan di file `PANDUAN_TIM_HUMANA.md`

## Struktur Project

src/main/java/com/humana/
├── model/      ← Class OOP
├── dao/        ← JDBC Database Access
├── servlet/    ← Controller
└── util/       ← Helper

src/main/webapp/
├── WEB-INF/views/  ← JSP (UI)
└── assets/         ← CSS, JS
```

### 1.4 Push ke GitHub

```bash
cd D:\KULIAH\PBO\TUBES\humana-web

git init
git add .
git commit -m "initial commit: project structure & master prompt"
git branch -M main
git remote add origin https://github.com/USERNAME_KAMU/humana-web.git
git push -u origin main
```

---

## BAGIAN 2 — Yang Dilakukan Setiap Anggota Tim

### Langkah 1 — Install Tools (Wajib)

Pastikan semua terinstall:

| Tool | Link Download | Cek |
|------|--------------|-----|
| JDK 17 | https://adoptium.net | `java -version` |
| Maven 3.9+ | https://maven.apache.org/download.cgi | `mvn -version` |
| Git | https://git-scm.com | `git --version` |
| VSCode | https://code.visualstudio.com | - |
| Tomcat 10 | https://tomcat.apache.org/download-10.cgi | - |

**VSCode Extensions yang wajib install:**
- Extension Pack for Java (Microsoft)
- Tomcat for Java

---

### Langkah 2 — Setup Environment Variable

**Maven (jika belum):**
1. Ekstrak Maven ke `C:\maven`
2. Tambahkan `C:\maven\bin` ke System PATH
3. Cek: `mvn -version`

**Java (jika belum):**
- Biasanya sudah otomatis saat install JDK
- Cek: `java -version`

---

### Langkah 3 — Clone Repository

```bash
git clone https://github.com/USERNAME_KAMU/humana-web.git
cd humana-web
code .
```

---

### Langkah 4 — Setup Database Credential

1. Salin file template:
```bash
# Windows CMD
copy src\main\resources\db.properties.example src\main\resources\db.properties

# PowerShell
Copy-Item src\main\resources\db.properties.example src\main\resources\db.properties
```

2. Buka `src/main/resources/db.properties`, isi dengan credential yang diberikan ketua tim:
```properties
db.url=jdbc:mariadb://mysql-xxxx.aivencloud.com:PORT/humana-dev
db.username=USERNAME_DARI_KETUA
db.password=PASSWORD_DARI_KETUA
```

> ⚠️ Minta credential ke ketua tim secara langsung (WhatsApp/Discord), JANGAN lewat GitHub.

---

### Langkah 5 — Download Dependencies

```bash
mvn dependency:resolve
```

Tunggu sampai BUILD SUCCESS. Maven akan download semua library otomatis.

---

### Langkah 6 — Build Project

```bash
mvn clean package
```

Jika BUILD SUCCESS, akan muncul file `target/humana-web.war`

---

### Langkah 7 — Setup Tomcat di VSCode

1. Ekstrak Tomcat 10 ke folder misal `C:\tomcat`
2. Di VSCode, buka panel **Tomcat for Java** (icon di sidebar kiri)
3. Klik **+** → pilih folder `C:\tomcat`
4. Klik kanan `humana-web.war` di folder `target/` → **"Run on Tomcat Server"**
5. Buka browser: `http://localhost:8080/humana-web/login`

---

### Langkah 8 — Verifikasi

Buka browser, akses:
```
http://localhost:8080/humana-web/login
```

Jika muncul halaman login → ✅ Setup berhasil!

---

## BAGIAN 3 — Workflow Kolaborasi Git

### Aturan Branch

```
main          ← branch utama, selalu stable
develop       ← branch integrasi tim
feature/xxx   ← branch fitur masing-masing
```

### Pembagian Tugas (Saran)

| Anggota | Tugas |
|---------|-------|
| A (Ketua) | Setup, model classes, DBConnection |
| B | DAO layer (GuruDAO, MuridDAO) |
| C | DAO layer (PemesananDAO, PembayaranDAO) |
| D | Servlet (Login, Register, Profil, Materi) |
| E | Servlet (Pesan, Bayar, Feedback, Histori) |
| Semua | JSP views masing-masing fitur |

### Alur Kerja Harian

```bash
# 1. Sebelum mulai kerja, selalu pull dulu
git pull origin develop

# 2. Buat branch fitur baru
git checkout -b feature/nama-fitur

# 3. Kerja, coding...

# 4. Selesai, commit
git add .
git commit -m "feat: tambah GuruDAO dan implementasinya"

# 5. Push branch
git push origin feature/nama-fitur

# 6. Buat Pull Request ke branch develop di GitHub
```

### Pesan Commit yang Baik

```
feat: tambah fitur baru
fix: perbaiki bug
refactor: refactor kode
style: perubahan UI/CSS
docs: update dokumentasi
```

---

## BAGIAN 4 — Troubleshooting Umum

| Masalah | Solusi |
|---------|--------|
| `mvn` not recognized | Tambahkan `C:\maven\bin` ke PATH, restart terminal |
| BUILD FAILURE dependency | Cek koneksi internet, jalankan `mvn dependency:resolve` ulang |
| `db.properties` not found | Pastikan file ada di `src/main/resources/` |
| Koneksi DB gagal | Cek credential, pastikan IP di-whitelist di hosting |
| Port 8080 sudah dipakai | Ganti port Tomcat di `C:\tomcat\conf\server.xml` |
| JSP tidak update | Jalankan `mvn clean package` ulang, restart Tomcat |

---

## BAGIAN 5 — Kontak & Info

- **Database**: MariaDB hosted di Aiven Cloud
- **Credential DB**: Minta ke ketua tim
- **Master Prompt AI**: Ada di file `HUMANA_MASTER_PROMPT.md`
- **Branch utama**: `main` (stable) dan `develop` (development)
