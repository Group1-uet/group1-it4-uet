import java.util.ArrayList;
import java.util.List;

public class Seller extends User{
    List<Items> sellItems = new ArrayList<>();

    public Seller(String id, String username, String password, String role) {
        super(id, username, password, "SELLER");
    }

    public void add(Items item) {
        sellItems.add(item);
    }

    public void remove(Items item) {
        sellItems.remove(item);
    }
}
