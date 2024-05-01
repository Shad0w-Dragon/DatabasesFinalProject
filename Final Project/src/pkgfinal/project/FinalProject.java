import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class FinalProject {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        
        System.out.println("Welcome to the library system!");
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        try {
            Connection conn = DatabaseConnector.getConnection();

            
            boolean isAdminAuthenticated = DatabaseConnector.authenticateAdmin(email, password);
            if (isAdminAuthenticated) {
                System.out.println("Admin login successful!");
            } else {
               
                boolean isMemberAuthenticated = DatabaseConnector.authenticateMember(email, password);
                if (isMemberAuthenticated) {
                    System.out.println("Member login successful!");
                } else {
                    System.out.println("Invalid email or password. Please try again.");
                }
            }

            conn.close();
        } catch (SQLException ex) {
            System.out.println("Error connecting to the database: " + ex.getMessage());
        } finally {
            scanner.close();
        }
    }
}
