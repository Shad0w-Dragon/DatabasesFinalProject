import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.ArrayList;

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

            // Authenticate user
            int userID = DatabaseConnector.authenticateUser(conn, email, password);
            if (userID != -1) {
                System.out.println("Login successful!");
                // Check if admin
                boolean isAdmin = isAdmin(conn, email);
                if (isAdmin) {
                    // Display admin actions
                    displayAdminActions(conn);
                } else {
                    // Display member actions
                    displayMemberActions(conn, userID);
                }
            } else {
                System.out.println("Login failed. Please check your email and password.");
            }
        } catch (SQLException ex) {
            System.out.println("Error connecting to the database: " + ex.getMessage());
        }
    }

    private static boolean isAdmin(Connection conn, String email) throws SQLException {
        // Check if the user is an admin
        String sql = "SELECT COUNT(*) AS adminCount FROM Admin WHERE Email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int adminCount = rs.getInt("adminCount");
                    return adminCount > 0;
                }
            }
        }
        return false;
    }

    public static void displayAdminActions(Connection conn) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nSelect an action:");
            System.out.println("1. Display all members");
            System.out.println("2. Display user checkout history");
            System.out.println("3. Delete a user");
            System.out.println("4. Create a user");
            System.out.println("5. Update a user");
            System.out.println("6. Delete a book");
            System.out.println("7. Create a book");
            System.out.println("8. Update a book");
            System.out.println("9. Delete an author");
            System.out.println("10. Create an author");
            System.out.println("11. Update an author");
            System.out.println("12. Forcefully check in a book");
            System.out.println("13. Exit");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline character

            switch (choice) {
                case 1:
                    displayAllMembers(conn);
                    break;
                case 2:
                    displayUserCheckoutHistory(conn);
                    break;
                case 3:
                    deleteUser(conn);
                    break;
                case 4:
                    createUser(conn);
                    break;
                case 5:
                    updateUser(conn);
                    break;

                case 13:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Method to display all members
    public static void displayAllMembers(Connection conn) {
        try {
            String query = "SELECT * FROM Members";
            try (PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
                System.out.println("All members:");
                System.out.println("UserID\tName\tEmail\tPassword\tMembershipStatus");
                while (rs.next()) {
                    int userID = rs.getInt("UserID");
                    String name = rs.getString("Name");
                    String email = rs.getString("Email");
                    String password = rs.getString("Password");
                    String membershipStatus = rs.getString("MembershipStatus");
                    System.out.printf("%d\t%s\t%s\t%s\t%s\n", userID, name, email, password, membershipStatus);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error displaying all members: " + ex.getMessage());
        }
    }

    public static void createUser(Connection conn) {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter user name: ");
            String name = scanner.nextLine();

            System.out.print("Enter user email: ");
            String email = scanner.nextLine();

            System.out.print("Enter membership status: ");
            String membershipStatus = scanner.nextLine();

            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            String query = "INSERT INTO Members (Name, Email, MembershipStatus, Password) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, name);
                stmt.setString(2, email);
                stmt.setString(3, membershipStatus);
                stmt.setString(4, password);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("User created successfully!");
                } else {
                    System.out.println("Failed to create user.");
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error creating user: " + ex.getMessage());
        }
    }

    public static void updateUser(Connection conn) {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter user ID to update: ");
            int userID = scanner.nextInt();
            scanner.nextLine(); // Consume newline character

            System.out.print("Enter new name (or SKIP for no change): ");
            String newName = scanner.nextLine();

            System.out.print("Enter new email (or SKIP for no change): ");
            String newEmail = scanner.nextLine();

            System.out.print("Enter new membership status (or SKIP for no change): ");
            String newMembershipStatus = scanner.nextLine();

            System.out.print("Enter new password (or SKIP for no change): ");
            String newPassword = scanner.nextLine();

            StringBuilder updateQuery = new StringBuilder("UPDATE Members SET ");
            ArrayList < String > params = new ArrayList < > (); // List to store parameters

            if (!newName.equalsIgnoreCase("SKIP")) {
                updateQuery.append("Name = ?");
                params.add(newName);
            }
            if (!newEmail.equalsIgnoreCase("SKIP")) {
                if (params.size() > 0) {
                    updateQuery.append(", "); // Add comma separator if needed
                }
                updateQuery.append("Email = ?");
                params.add(newEmail);
            }
            if (!newMembershipStatus.equalsIgnoreCase("SKIP")) {
                if (params.size() > 0) {
                    updateQuery.append(", "); // Add comma separator if needed
                }
                updateQuery.append("MembershipStatus = ?");
                params.add(newMembershipStatus);
            }
            if (!newPassword.equalsIgnoreCase("SKIP")) {
                if (params.size() > 0) {
                    updateQuery.append(", "); // Add comma separator if needed
                }
                updateQuery.append("Password = ?");
                params.add(newPassword);
            }

            // Append the WHERE clause to specify the user to update
            updateQuery.append(" WHERE UserID = ?");

            try (PreparedStatement stmt = conn.prepareStatement(updateQuery.toString())) {
                // Bind parameters
                for (int i = 0; i < params.size(); i++) {
                    stmt.setString(i + 1, params.get(i));
                }
                // Bind UserID parameter
                stmt.setInt(params.size() + 1, userID);

                // Execute the update statement
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("User updated successfully!");
                } else {
                    System.out.println("Failed to update user. Please try again.");
                }
            } catch (SQLException ex) {

            }

            try (PreparedStatement stmt = conn.prepareStatement(updateQuery.toString())) {
                stmt.setInt(1, userID);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("User updated successfully!");
                } else {
                    System.out.println("Failed to update user.");
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error updating user: " + ex.getMessage());
        }
    }
    public static void deleteUser(Connection conn) {
        Scanner scanner = new Scanner(System.in);
        try {
            // Display all users
            displayAllMembers(conn);

            // Ask for user confirmation
            System.out.print("\nEnter the userID of the user you want to delete: ");
            int userIDToDelete = 0;
            try {
                userIDToDelete = scanner.nextInt();
                scanner.nextLine(); // Consume newline character
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid userID.");
                return; // Return to menu
            }

            System.out.print("Are you sure you want to delete this user? (yes/no): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if (confirmation.equals("yes")) {
                // Delete associated checkouts first
                String deleteCheckoutsQuery = "DELETE FROM Checkout WHERE UserID = ?";
                try (PreparedStatement deleteCheckoutsStmt = conn.prepareStatement(deleteCheckoutsQuery)) {
                    deleteCheckoutsStmt.setInt(1, userIDToDelete);
                    int checkoutsDeleted = deleteCheckoutsStmt.executeUpdate();
                    if (checkoutsDeleted > 0) {
                        System.out.println("Associated checkouts deleted successfully!");
                    } else {
                        System.out.println("No associated checkouts found.");
                    }
                }

                // Now delete the user
                String deleteUserQuery = "DELETE FROM Members WHERE UserID = ?";
                try (PreparedStatement deleteUserStmt = conn.prepareStatement(deleteUserQuery)) {
                    deleteUserStmt.setInt(1, userIDToDelete);
                    int rowsAffected = deleteUserStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("User deleted successfully!");
                    } else {
                        System.out.println("Failed to delete user.");
                    }
                }
            } else {
                System.out.println("Deletion cancelled.");
            }
        } catch (SQLException ex) {
            System.out.println("Error deleting user: " + ex.getMessage());
        }
    }




    public static void displayUserCheckoutHistory(Connection conn) {
        try {
            String query = "SELECT * FROM UserCheckoutHistory";
            try (PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
                System.out.println("User Checkout History:");
                System.out.println("UserName\tBookTitle\tDueDate");
                while (rs.next()) {
                    String userName = rs.getString("UserName");
                    String bookTitle = rs.getString("BookTitle");
                    String dueDate = rs.getString("DueDate");
                    System.out.printf("%-10s\t%-20s\t%s\n", userName, bookTitle, dueDate);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error displaying user checkout history: " + ex.getMessage());
        }
    }

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
            try (PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
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

}