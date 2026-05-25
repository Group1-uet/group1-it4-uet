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
            pstmt.setString(3, auction.getStartTime().toString());
            pstmt.setString(4, auction.getEndTime().toString());
            pstmt.setDouble(5, auction.getCurrentHighestBid());
            pstmt.setString(6, auction.getStatus());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Auction getAuctionById(String id) {
        String sql = "SELECT a.*, i.seller_id, i.name AS item_name, i.description AS item_description, " +
                     "i.starting_price, " +
                     "(SELECT COUNT(*) FROM bids WHERE auction_id = a.id) AS bid_count, " +
                     "(SELECT bidder_id FROM bids WHERE auction_id = a.id ORDER BY amount DESC LIMIT 1) AS highest_bidder " +
                     "FROM auctions a JOIN items i ON a.item_id = i.id " +
                     "WHERE a.id = ?";
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
        String sql = "SELECT a.*, i.seller_id, i.name AS item_name, i.description AS item_description, " +
                     "i.starting_price, " +
                     "(SELECT COUNT(*) FROM bids WHERE auction_id = a.id) AS bid_count, " +
                     "(SELECT bidder_id FROM bids WHERE auction_id = a.id ORDER BY amount DESC LIMIT 1) AS highest_bidder " +
                     "FROM auctions a JOIN items i ON a.item_id = i.id";
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
            pstmt.setString(1, auction.getStartTime().toString());
            pstmt.setString(2, auction.getEndTime().toString());
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
        String startTimeStr = rs.getString("start_time");
        String endTimeStr = rs.getString("end_time");
        LocalDateTime startTime = startTimeStr != null ? LocalDateTime.parse(startTimeStr) : null;
        LocalDateTime endTime = endTimeStr != null ? LocalDateTime.parse(endTimeStr) : null;
        double currentPrice = rs.getDouble("current_price");
        String status = rs.getString("status");
        String sellerId = rs.getString("seller_id");
        String itemName = rs.getString("item_name");
        String itemDescription = rs.getString("item_description");
        int bidCount = rs.getInt("bid_count");
        String highestBidder = rs.getString("highest_bidder");

        Auction auction = new Auction(id, itemId, sellerId, startTime, endTime);
        auction.setCurrentHighestBid(currentPrice);
        // starting_price comes from items table via JOIN
        try { auction.setStartingPrice(rs.getDouble("starting_price")); } catch (SQLException ignored) {
            auction.setStartingPrice(currentPrice); // fallback
        }
        auction.setStatus(status);
        auction.setItemName(itemName);
        auction.setItemDescription(itemDescription);
        auction.setBidCount(bidCount);
        auction.setCurrentHighestBidder(highestBidder);
        return auction;
    }
}
