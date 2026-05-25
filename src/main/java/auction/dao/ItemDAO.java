package auction.dao;

import auction.db.DatabaseConnection;
import auction.model.Art;
import auction.model.Electronics;
import auction.model.Item;
import auction.model.Vehicle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

    public void createItem(Item item) {
        String sql = "INSERT INTO items (id, seller_id, name, description, starting_price, item_type) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item.getId());
            pstmt.setString(2, item.getSellerId());
            pstmt.setString(3, item.getName());
            pstmt.setString(4, item.getDescription());
            pstmt.setDouble(5, item.getStartingPrice());
            pstmt.setString(6, item.getCategory());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Item getItemById(String id) {
        String sql = "SELECT * FROM items WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToItem(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public void updateItem(Item item) {
        String sql = "UPDATE items SET name = ?, description = ?, starting_price = ?, item_type = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item.getName());
            pstmt.setString(2, item.getDescription());
            pstmt.setDouble(3, item.getStartingPrice());
            pstmt.setString(4, item.getCategory());
            pstmt.setString(5, item.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteItem(String id) {
        String sql = "DELETE FROM items WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Item mapResultSetToItem(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String sellerId = rs.getString("seller_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        double startingPrice = rs.getDouble("starting_price");
        String itemType = rs.getString("item_type");

        switch (itemType.toUpperCase()) {
            case "ELECTRONICS":
                return new Electronics(id, name, description, startingPrice, sellerId);
            case "ART":
                return new Art(id, name, description, startingPrice, sellerId);
            case "VEHICLE":
                return new Vehicle(id, name, description, startingPrice, sellerId);
            default:
                // Default fallback if type is unknown but we need to return an item
                // Can create a GenericItem class if needed, or throw exception
                throw new IllegalArgumentException("Unknown item type: " + itemType);
        }
    }
}
