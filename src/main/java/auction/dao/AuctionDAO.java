package auction.dao;

import auction.db.DatabaseConnection;
import auction.model.Auction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuctionDAO {

    public void createAuction(Auction auction) {
        String sql = "INSERT INTO auctions (id, item_id, start_time, end_time, current_price, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, auction.getAuctionId());
            pstmt.setString(2, auction.getItemId());
            pstmt.setObject(3, auction.getStartTime());
            pstmt.setObject(4, auction.getEndTime());
            pstmt.setDouble(5, auction.getCurrentHighestBid());
            pstmt.setString(6, auction.getStatus());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Auction getAuctionById(String id) {
        String sql = "SELECT * FROM auctions WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToAuction(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Auction> getAllAuctions() {
        List<Auction> auctions = new ArrayList<>();
        String sql = "SELECT * FROM auctions";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                auctions.add(mapResultSetToAuction(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return auctions;
    }

    public void updateAuction(Auction auction) {
        String sql = "UPDATE auctions SET start_time = ?, end_time = ?, current_price = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, auction.getStartTime());
            pstmt.setObject(2, auction.getEndTime());
            pstmt.setDouble(3, auction.getCurrentHighestBid());
            pstmt.setString(4, auction.getStatus());
            pstmt.setString(5, auction.getAuctionId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAuction(String id) {
        String sql = "DELETE FROM auctions WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Auction mapResultSetToAuction(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String itemId = rs.getString("item_id");
        LocalDateTime startTime = rs.getObject("start_time", LocalDateTime.class);
        LocalDateTime endTime = rs.getObject("end_time", LocalDateTime.class);
        double currentPrice = rs.getDouble("current_price");
        String status = rs.getString("status");

        // We need sellerId to create Auction, but it's not in the auctions table directly.
        // We'll pass a placeholder or we would need a JOIN. For now, passing empty string.
        Auction auction = new Auction(id, itemId, "", startTime, endTime);
        auction.setCurrentHighestBid(currentPrice);
        auction.setStatus(status);
        return auction;
    }
}
