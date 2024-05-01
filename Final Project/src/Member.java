public class Member {
    private int userID;
    private String name;
    private String email;
    private String password;

    
    public boolean login(String email, String password) {
        return DatabaseConnector.authenticateMember(email, password);
    }
}