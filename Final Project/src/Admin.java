
public class Admin {
    private int adminID;
    private String name;
    private String email;
    private String password;

   
    public boolean login(String email, String password) {
        return DatabaseConnector.authenticateAdmin(email, password);
    }
}