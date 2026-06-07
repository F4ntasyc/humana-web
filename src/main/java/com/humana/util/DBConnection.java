package com.humana.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Utility class untuk mengelola koneksi database JDBC ke MariaDB.
 * Menggunakan singleton pattern untuk memuat konfigurasi dari db.properties
 * satu kali saja, lalu menyediakan method getConnection() untuk seluruh aplikasi.
 *
 * <p>Adaptasi dari: backend/src/database.js (Node.js mariadb pool)</p>
 *
 * <p>Penggunaan:
 * <pre>
 *     try (Connection conn = DBConnection.getConnection()) {
 *         // operasi database
 *     }
 * </pre>
 * </p>
 */
public class DBConnection {

    private static final Properties properties = new Properties();

    // Static initializer — dijalankan sekali saat class pertama kali di-load
    static {
        try (InputStream input = DBConnection.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException(
                    "File db.properties tidak ditemukan di classpath (src/main/resources/).");
            }
            properties.load(input);

            // Load JDBC driver secara eksplisit untuk kompatibilitas Tomcat
            Class.forName("org.mariadb.jdbc.Driver");

        } catch (IOException e) {
            throw new RuntimeException("Gagal membaca file db.properties: " + e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MariaDB JDBC Driver tidak ditemukan. "
                + "Pastikan mariadb-java-client ada di pom.xml.", e);
        }
    }

    /**
     * Mendapatkan koneksi baru ke database.
     * WAJIB ditutup setelah digunakan — gunakan try-with-resources.
     *
     * @return Connection aktif ke database MariaDB
     * @throws SQLException jika koneksi gagal
     */
    public static Connection getConnection() throws SQLException {
        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.user");
        String password = properties.getProperty("db.password");

        return DriverManager.getConnection(url, user, password);
    }

    // Mencegah instansiasi
    private DBConnection() {
    }
}
