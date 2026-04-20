public class Admin extends User{
    public Admin(String id, String username, String password, String role) {
        super(id, username, password, "ADMIN");
    }
}
