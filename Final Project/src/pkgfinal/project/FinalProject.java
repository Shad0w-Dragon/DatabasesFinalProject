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

        try {
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
                case 6:
                    displayAllBooksWithISBN(conn);
                    deleteBook(conn);
                    break;
                case 7:
                    createBook(conn);
                    break;
                case 8:
                    updateBook(conn);
                    break;
                case 9:
                    deleteAuthor(conn);
                    break;
                case 10:
                    createAuthor(conn);
                    break;
                case 11:
                    updateAuthor(conn);
                    break;
                case 12:
                    //forcefullyCheckInBook(conn);
                    break;
                case 13:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}


public static void createAuthor(Connection conn) throws SQLException {
    Scanner scanner = new Scanner(System.in);

    System.out.print("Enter the First Name of the new author: ");
    String firstName = scanner.nextLine();
    System.out.print("Enter the Last Name of the new author: ");
    String lastName = scanner.nextLine();
    System.out.print("Enter the Date of Birth of the new author (YYYY-MM-DD): ");
    String dateOfBirth = scanner.nextLine();
    System.out.print("Enter the Common Genre of the new author: ");
    String commonGenre = scanner.nextLine();

    String insertQuery = "INSERT INTO Author (FirstName, LastName, DateOfBirth, CommonGenre) " +
            "VALUES (?, ?, ?, ?)";
    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
        insertStmt.setString(1, firstName);
        insertStmt.setString(2, lastName);
        insertStmt.setString(3, dateOfBirth);
        insertStmt.setString(4, commonGenre);
        int rowsAffected = insertStmt.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Author created successfully!");
        } else {
            System.out.println("Failed to create author. Please try again.");
        }
    }
}

public static void updateAuthor(Connection conn) throws SQLException {
    Scanner scanner = new Scanner(System.in);

    System.out.print("Enter the AuthorID of the author you want to update: ");
    int authorID = scanner.nextInt();
    scanner.nextLine(); // Consume newline character

    System.out.print("Enter new First Name (or SKIP for no change): ");
    String newFirstName = scanner.nextLine();
    System.out.print("Enter new Last Name (or SKIP for no change): ");
    String newLastName = scanner.nextLine();
    System.out.print("Enter new Date of Birth (YYYY-MM-DD) (or SKIP for no change): ");
    String newDateOfBirth = scanner.nextLine();
    System.out.print("Enter new Common Genre (or SKIP for no change): ");
    String newCommonGenre = scanner.nextLine();

    StringBuilder updateQuery = new StringBuilder("UPDATE Author SET ");
    if (!newFirstName.equalsIgnoreCase("SKIP")) {
        updateQuery.append("FirstName = '").append(newFirstName).append("'");
    }
    if (!newLastName.equalsIgnoreCase("SKIP")) {
        if (updateQuery.length() > 15) {
            updateQuery.append(", ");
        }
        updateQuery.append("LastName = '").append(newLastName).append("'");
    }
    if (!newDateOfBirth.equalsIgnoreCase("SKIP")) {
        if (updateQuery.length() > 15) {
            updateQuery.append(", ");
        }
        updateQuery.append("DateOfBirth = '").append(newDateOfBirth).append("'");
    }
    if (!newCommonGenre.equalsIgnoreCase("SKIP")) {
        if (updateQuery.length() > 15) {
            updateQuery.append(", ");
        }
        updateQuery.append("CommonGenre = '").append(newCommonGenre).append("'");
    }
    updateQuery.append(" WHERE AuthorID = ?");
    
    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery.toString())) {
        updateStmt.setInt(1, authorID);
        int rowsAffected = updateStmt.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Author updated successfully!");
        } else {
            System.out.println("Failed to update author. Please check the AuthorID and try again.");
        }
    }
}

public static void deleteAuthor(Connection conn) throws SQLException {
    Scanner scanner = new Scanner(System.in);

    System.out.print("Enter the AuthorID of the author you want to delete: ");
    int authorID = scanner.nextInt();

    // First, uncheckout the books from users
    String uncheckoutQuery = "DELETE FROM Checkout WHERE ISBN IN (SELECT ISBN FROM Book WHERE AuthorID = ?)";
    try (PreparedStatement uncheckoutStmt = conn.prepareStatement(uncheckoutQuery)) {
        uncheckoutStmt.setInt(1, authorID);
        int uncheckoutRowsAffected = uncheckoutStmt.executeUpdate();
        if (uncheckoutRowsAffected > 0) {
            System.out.println("Books unchecked out from users successfully!");
        }
    }

    // Then, delete the author's books from the Book table
    String deleteBooksQuery = "DELETE FROM Book WHERE AuthorID = ?";
    try (PreparedStatement deleteBooksStmt = conn.prepareStatement(deleteBooksQuery)) {
        deleteBooksStmt.setInt(1, authorID);
        int booksRowsAffected = deleteBooksStmt.executeUpdate();
        if (booksRowsAffected > 0) {
            System.out.println("Books by the author deleted successfully!");
        } else {
            System.out.println("No books found by the author.");
        }
    }

    // Finally, delete the author from the Author table
    String deleteAuthorQuery = "DELETE FROM Author WHERE AuthorID = ?";
    try (PreparedStatement deleteAuthorStmt = conn.prepareStatement(deleteAuthorQuery)) {
        deleteAuthorStmt.setInt(1, authorID);
        int authorRowsAffected = deleteAuthorStmt.executeUpdate();
        if (authorRowsAffected > 0) {
            System.out.println("Author deleted successfully!");
        } else {
            System.out.println("Failed to delete author. Please check the AuthorID and try again.");
        }
    }
}


  // Method to display all books with their ISBNs
public static void displayAllBooksWithISBN(Connection conn) {
    try {
        String query = "SELECT ISBN, Title FROM Book";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            System.out.println("Books available in the library:");
            System.out.println("ISBN\t\tTitle");
            while (rs.next()) {
                String isbn = rs.getString("ISBN");
                String title = rs.getString("Title");
                System.out.printf("%s\t%s\n", isbn, title);
            }
        }
    } catch (SQLException ex) {
        System.out.println("Error displaying books: " + ex.getMessage());
    }
}

// Method to delete a book
public static void deleteBook(Connection conn) throws SQLException {
    Scanner scanner = new Scanner(System.in);

    System.out.print("Enter the ISBN of the book you want to delete: ");
    String isbn = scanner.nextLine();

    // First, delete all associated checkouts from the Checkout table
    String deleteCheckoutQuery = "DELETE FROM Checkout WHERE ISBN = ?";
    try (PreparedStatement deleteCheckoutStmt = conn.prepareStatement(deleteCheckoutQuery)) {
        deleteCheckoutStmt.setString(1, isbn);
        int checkoutRowsAffected = deleteCheckoutStmt.executeUpdate();
        System.out.println(checkoutRowsAffected + " checkouts deleted for the book.");
    }

    // Next, delete the book from the Book table
    String deleteBookQuery = "DELETE FROM Book WHERE ISBN = ?";
    try (PreparedStatement deleteBookStmt = conn.prepareStatement(deleteBookQuery)) {
        deleteBookStmt.setString(1, isbn);
        int bookRowsAffected = deleteBookStmt.executeUpdate();
        if (bookRowsAffected > 0) {
            System.out.println("Book deleted successfully!");
        } else {
            System.out.println("Failed to delete book. Please check the ISBN and try again.");
        }
    }
}


// Method to create a book
public static void createBook(Connection conn) throws SQLException {
    Scanner scanner = new Scanner(System.in);

    System.out.print("Enter the ISBN of the new book: ");
    String isbn = scanner.nextLine();
    System.out.print("Enter the title of the new book: ");
    String title = scanner.nextLine();
    System.out.print("Enter the author ID of the new book: ");
    int authorID = scanner.nextInt();
    scanner.nextLine(); // Consume newline character
    System.out.print("Enter the publisher of the new book: ");
    String publisher = scanner.nextLine();
    System.out.print("Enter the publish time of the new book: ");
    String publishTime = scanner.nextLine();
    System.out.print("Enter the description of the new book: ");
    String description = scanner.nextLine();
    System.out.print("Enter the genre of the new book: ");
    String genre = scanner.nextLine();

    String insertQuery = "INSERT INTO Book (ISBN, Title, AuthorID, Publisher, PublishTime, Description, Genre) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";
    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
        insertStmt.setString(1, isbn);
        insertStmt.setString(2, title);
        insertStmt.setInt(3, authorID);
        insertStmt.setString(4, publisher);
        insertStmt.setString(5, publishTime);
        insertStmt.setString(6, description);
        insertStmt.setString(7, genre);
        int rowsAffected = insertStmt.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Book created successfully!");
        } else {
            System.out.println("Failed to create book. Please try again.");
        }
    }
}


// Method to update a book
public static void updateBook(Connection conn) throws SQLException {
    Scanner scanner = new Scanner(System.in);

    System.out.print("Enter the ISBN of the book you want to update: ");
    String isbn = scanner.nextLine();
    System.out.print("Enter new title (or SKIP for no change): ");
    String newTitle = scanner.nextLine();
    System.out.print("Enter new author ID (or SKIP for no change): ");
    String newAuthorID = scanner.nextLine();
    System.out.print("Enter new publisher (or SKIP for no change): ");
    String newPublisher = scanner.nextLine();
    System.out.print("Enter new publish time (or SKIP for no change): ");
    String newPublishTime = scanner.nextLine();
    System.out.print("Enter new description (or SKIP for no change): ");
    String newDescription = scanner.nextLine();

    StringBuilder updateQuery = new StringBuilder("UPDATE Book SET ");
    if (!newTitle.equalsIgnoreCase("SKIP")) {
        updateQuery.append("Title = '").append(newTitle).append("'");
    }
    // Add similar conditions for other fields
    if (!newAuthorID.equalsIgnoreCase("SKIP")) {
        updateQuery.append(", AuthorID = '").append(newAuthorID).append("'");
    }
    if (!newPublisher.equalsIgnoreCase("SKIP")) {
        updateQuery.append(", Publisher = '").append(newPublisher).append("'");
    }
    if (!newPublishTime.equalsIgnoreCase("SKIP")) {
        updateQuery.append(", PublishTime = '").append(newPublishTime).append("'");
    }
    if (!newDescription.equalsIgnoreCase("SKIP")) {
        updateQuery.append(", Description = '").append(newDescription).append("'");
    }

    updateQuery.append(" WHERE ISBN = ?");

    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery.toString())) {
        updateStmt.setString(1, isbn);
        int rowsAffected = updateStmt.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Book updated successfully!");
        } else {
            System.out.println("Failed to update book. Please check the ISBN and try again.");
        }
    }
}

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
                            System.out.println("Failed to return boadmok. Please try again.");
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error returning book: " + ex.getMessage());
        }
    }

}