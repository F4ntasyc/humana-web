import java.sql.*;
public class TestDB2 {
    public static void main(String[] args) throws Exception {
        Class.forName("org.mariadb.jdbc.Driver");
        Connection c = DriverManager.getConnection("jdbc:mariadb://mysql-22e5161d-humana-dev.f.aivencloud.com:13702/humana-dev", "avnadmin", "AVNS_m8oRx2wISjUBkSfkl72");
        System.out.println("MateriGuru for id_guru=1:");
        ResultSet rs = c.createStatement().executeQuery("SELECT * FROM MateriGuru WHERE id_guru = 1");
        while(rs.next()) {
            System.out.println(rs.getInt("id_materi"));
        }
        System.out.println("Pemesanan with id_guru=1:");
        ResultSet rs2 = c.createStatement().executeQuery("SELECT id_pemesanan, status_pemesanan FROM Pemesanan WHERE id_guru = 1");
        while(rs2.next()) {
            System.out.println(rs2.getInt(1) + " | " + rs2.getString(2));
        }
    }
}
