import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnector {

    public static boolean authenticateAdmin(String email, String password) {
        String sql = "SELECT * FROM Admin WHERE Email = ? AND Password = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); 
            }
        } catch (SQLException ex) {
            System.out.println("Error authenticating admin: " + ex.getMessage());
            return false;
        }
    }

    public static boolean authenticateMember(String email, String password) {
        String sql = "SELECT * FROM Members WHERE Email = ? AND Password = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            System.out.println("Error authenticating member: " + ex.getMessage());
            return false;
        }
    }

     public static Connection getConnection() throws SQLException {
        String host = "localhost";
        int port = 5432;
        String databaseName = "Assignment 8";
        String username = "postgres";
        String password = "Derpy01!";
        
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + databaseName;
        
        return DriverManager.getConnection(url, username, password);
    }
}
