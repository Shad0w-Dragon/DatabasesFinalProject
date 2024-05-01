import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class FinalProject {

    public static void main(String[] args) {
        try (Connection conn = DatabaseConnector.getConnection()) {
            System.out.println("Connected to the database!");
            Scanner scanner = new Scanner(System.in);
            System.out.println("Welcome to the Library System!");

            // Login
            System.out.print("Enter your email: ");
            String email = scanner.nextLine();
            System.out.print("Enter your password: ");
            String password = scanner.nextLine();

            int userID = DatabaseConnector.authenticateMember(conn, email, password);
            if (userID != -1) {
                System.out.println("Login successful!");

                // Display member actions
                displayMemberActions(conn, userID);
            } else {
                System.out.println("Login failed. Please check your email and password.");
            }
        } catch (SQLException ex) {
            System.out.println("Error connecting to the database: " + ex.getMessage());
        }
    }

    // Method to authenticate a member
    public static int authenticateMember(Connection conn, String email, String password) {
        try {
            String query = "SELECT UserID FROM Members WHERE Email = ? AND Password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, email);
                stmt.setString(2, password);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("UserID");
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error authenticating member: " + ex.getMessage());
        }
        return -1; // Authentication failed
    }

    // Method to display member actions
    public static void displayMemberActions(Connection conn, int userID) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nSelect an action:");
            System.out.println("1. View books");
            System.out.println("2. Check out a book");
            System.out.println("3. Return a book");
            System.out.println("4. Exit");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline character

            switch (choice) {
                case 1:
                    viewBooks(conn);
                    break;
                case 2:
                    checkOutBook(conn, userID);
                    break;
                case 3:
                    returnBook(conn, userID);
                    break;
                case 4:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }


public static void viewBooks(Connection conn) {
    try {
        String query = "SELECT * FROM BookAuthors";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            System.out.println("Books available in the library:");
            System.out.println("Title\t\tGenre\t\tAuthor");
            while (rs.next()) {
                String title = rs.getString("Title");
                String genre = rs.getString("Genre");
                String author = rs.getString("Author");
                System.out.printf("%s\t%s\t%s\n", title, genre, author);
            }
        }
    } catch (SQLException ex) {
        System.out.println("Error viewing books: " + ex.getMessage());
    }
}


    // Method to check out a book
    public static void checkOutBook(Connection conn, int userID) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the title of the book you want to check out: ");
        String title = scanner.nextLine();

        try {
            String isbnQuery = "SELECT ISBN FROM Book WHERE Title = ?";
            try (PreparedStatement isbnStmt = conn.prepareStatement(isbnQuery)) {
                isbnStmt.setString(1, title);
                try (ResultSet isbnRs = isbnStmt.executeQuery()) {
                    if (isbnRs.next()) {
                        String isbn = isbnRs.getString("ISBN");
                        String countQuery = "SELECT COUNT(*) AS total FROM Checkout WHERE UserID = ?";
                        try (PreparedStatement countStmt = conn.prepareStatement(countQuery)) {
                            countStmt.setInt(1, userID);
                            try (ResultSet countRs = countStmt.executeQuery()) {
                                if (countRs.next() && countRs.getInt("total") >= 3) {
                                    System.out.println("You already have 3 or more books checked out. Cannot check out more.");
                                } else {
                                    String insertQuery = "INSERT INTO Checkout (UserID, ISBN, DueDate) VALUES (?, ?, '2025-01-01')";
                                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                                        insertStmt.setInt(1, userID);
                                        insertStmt.setString(2, isbn);
                                        int rowsAffected = insertStmt.executeUpdate();
                                        if (rowsAffected > 0) {
                                            System.out.println("Book checked out successfully!");
                                        } else {
                                            System.out.println("Failed to check out book. Please try again.");
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        System.out.println("Book not found. Please enter a valid title.");
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error checking out book: " + ex.getMessage());
        }
    }

    // Method for members to return a book
    public static void returnBook(Connection conn, int userID) {
        Scanner scanner = new Scanner(System.in);

        try {
            String checkoutQuery = "SELECT c.CheckoutID, c.ISBN, b.Title FROM Checkout c JOIN Book b ON c.ISBN = b.ISBN WHERE c.UserID = ?";
            try (PreparedStatement checkoutStmt = conn.prepareStatement(checkoutQuery)) {
                checkoutStmt.setInt(1, userID);
                try (ResultSet checkoutRs = checkoutStmt.executeQuery()) {
                    if (!checkoutRs.isBeforeFirst()) {
                        System.out.println("No checkouts.");
                        return;
                    }

                    System.out.println("Your checked out books:");
                    System.out.println("CheckoutID\tISBN\t\tTitle");
                    while (checkoutRs.next()) {
                        int checkoutID = checkoutRs.getInt("CheckoutID");
                        String isbn = checkoutRs.getString("ISBN");
                        String title = checkoutRs.getString("Title");
                        System.out.printf("%-12d%-12s%-12s\n", checkoutID, isbn, title);
                    }

                    System.out.print("Enter the CheckoutID of the book you want to return (or 0 to cancel): ");
                    int checkoutIDToReturn = scanner.nextInt();

                    if (checkoutIDToReturn == 0) {
                        System.out.println("Return cancelled.");
                        return;
                    }

                    String deleteQuery = "DELETE FROM Checkout WHERE CheckoutID = ?";
                    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                        deleteStmt.setInt(1, checkoutIDToReturn);
                        int rowsAffected = deleteStmt.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Book returned successfully!");
                        } else {
                            System.out.println("Failed to return book. Please try again.");
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error returning book: " + ex.getMessage());
        }
    }

    // Other methods...
}
