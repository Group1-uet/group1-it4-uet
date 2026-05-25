package auction.dao;

import auction.db.DatabaseConnection;
import auction.model.BidTransaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BidTransactionDAO {

    public void createBidTransaction(BidTransaction bid) {
        String sql = "INSERT INTO bids (auction_id, bidder_id, amount, bid_time) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bid.getAuctionId());
            pstmt.setString(2, bid.getBidderId());
            pstmt.setDouble(3, bid.getBidAmount());
            pstmt.setObject(4, bid.getTimestamp());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<BidTransaction> getBidsByAuctionId(String auctionId) {
        List<BidTransaction> bids = new ArrayList<>();
        String sql = "SELECT * FROM bids WHERE auction_id = ? ORDER BY bid_time DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, auctionId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bids.add(mapResultSetToBidTransaction(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bids;
    }

    private BidTransaction mapResultSetToBidTransaction(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String auctionId = rs.getString("auction_id");
        String bidderId = rs.getString("bidder_id");
        double amount = rs.getDouble("amount");
        LocalDateTime bidTime = rs.getObject("bid_time", LocalDateTime.class);

        BidTransaction bid = new BidTransaction(String.valueOf(id), auctionId, bidderId, amount);
        bid.setTimestamp(bidTime);
        return bid;
    }
}
