import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnector {

    public static int authenticateUser(Connection conn, String email, String password) {
        String sql = "SELECT UserID FROM Members WHERE Email = ? AND Password = ? " +
                     "UNION " +
                     "SELECT AdminID FROM Admin WHERE Email = ? AND Password = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.setString(4, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1); // Return user ID if authentication succeeds
                } else {
                    return -1; // Return -1 if authentication fails
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error authenticating user: " + ex.getMessage());
            return -1; // Return -1 if an error occurs
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
