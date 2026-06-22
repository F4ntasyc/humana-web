import java.sql.*;
public class TestDB {
    public static void main(String[] args) throws Exception {
        Class.forName("org.mariadb.jdbc.Driver");
        Connection c = DriverManager.getConnection("jdbc:mariadb://mysql-22e5161d-humana-dev.f.aivencloud.com:13702/humana-dev", "avnadmin", "AVNS_m8oRx2wISjUBkSfkl72");
        System.out.println("MURID Jadwal Aktif Query Result:");
        ResultSet rs = c.createStatement().executeQuery(
            "SELECT p.id_pemesanan, p.status_pemesanan, p.lokasi_sesi, m.nama_materi " +
            "FROM Pemesanan p " +
            "JOIN Materi m ON m.id_materi = p.id_materi " +
            "JOIN MataPelajaran mp ON mp.id_mapel = m.id_mapel " +
            "JOIN Murid murid ON murid.id_murid = p.id_murid " +
            "LEFT JOIN Guru guru ON guru.id_guru = p.id_guru " +
            "LEFT JOIN Pembayaran bayar ON bayar.id_pemesanan = p.id_pemesanan " +
            "WHERE p.status_pemesanan IN ('menunggu konfirmasi', 'dikonfirmasi', 'berlangsung')"
        );
        while(rs.next()) {
            System.out.println(rs.getInt(1) + " | " + rs.getString(2) + " | " + rs.getString(3) + " | " + rs.getString(4));
        }

        System.out.println("GURU Permintaan Query Result:");
        ResultSet rs2 = c.createStatement().executeQuery(
            "SELECT DISTINCT p.id_pemesanan, p.status_pemesanan, p.lokasi_sesi, m.nama_materi " +
            "FROM Pemesanan p " +
            "JOIN Materi m ON m.id_materi = p.id_materi " +
            "JOIN MataPelajaran mp ON mp.id_mapel = m.id_mapel " +
            "JOIN Murid murid ON murid.id_murid = p.id_murid " +
            "LEFT JOIN Guru guru ON guru.id_guru = p.id_guru " +
            "LEFT JOIN Pembayaran bayar ON bayar.id_pemesanan = p.id_pemesanan " +
            "LEFT JOIN MateriGuru mg ON mg.id_materi = p.id_materi " +
            "WHERE p.status_pemesanan IN ('menunggu konfirmasi', 'dikonfirmasi', 'berlangsung')"
        );
        while(rs2.next()) {
            System.out.println(rs2.getInt(1) + " | " + rs2.getString(2) + " | " + rs2.getString(3) + " | " + rs2.getString(4));
        }
    }
}
