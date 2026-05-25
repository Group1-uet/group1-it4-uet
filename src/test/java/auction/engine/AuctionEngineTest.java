package auction.engine;

import auction.db.DatabaseConnection;
import auction.dao.AuctionDAO;
import auction.dao.ItemDAO;
import auction.dao.UserDAO;
import auction.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class AuctionEngineTest {

    private static UserDAO userDAO;
    private static ItemDAO itemDAO;
    private static AuctionDAO auctionDAO;
    private static AuctionEngine engine;

    private Bidder bidder1;
    private Bidder bidder2;
    private Seller seller;
    private Item item;

    @BeforeAll
    public static void setUpClass() {
        // Use a test-specific SQLite file to persist schema across connections
        DatabaseConnection.setUrl("jdbc:sqlite:test_auction_db.sqlite");
        DatabaseConnection.initializeDatabase();

        userDAO = new UserDAO();
        itemDAO = new ItemDAO();
        auctionDAO = new AuctionDAO();
        engine = AuctionEngine.getInstance();
    }

    @BeforeEach
    public void setUp() throws Exception {
        // Clean database tables before each test
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM bids;");
            stmt.execute("DELETE FROM auctions;");
            stmt.execute("DELETE FROM items;");
            stmt.execute("DELETE FROM users;");
        }

        // Create sample users
        bidder1 = new Bidder("bidder1", "bidder1_user", "password123", "bidder1@example.com", 5000.0);
        bidder2 = new Bidder("bidder2", "bidder2_user", "password123", "bidder2@example.com", 8000.0);
        seller = new Seller("seller1", "seller_user", "password123", "seller@example.com", "MyStore");

        userDAO.createUser(bidder1);
        userDAO.createUser(bidder2);
        userDAO.createUser(seller);

        AutoBidManager.getInstance().clear();
        // Create sample item
        item = new Electronics("item1", "Laptop", "Gaming Laptop", 1000.0, "seller1");
        itemDAO.createItem(item);
    }

    @Test
    public void testPlaceBidSuccess() {
        // Create an active running auction
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        Auction auction = new Auction("auction1", "item1", "seller1", start, end);
        auction.setCurrentHighestBid(1000.0);
        auction.setStartingPrice(1000.0);
        auction.setStatus("RUNNING");
        auctionDAO.createAuction(auction);

        // Bidder 1 places a valid bid
        boolean result = engine.placeBid("auction1", "bidder1", 1200.0);
        assertTrue(result, "Place bid should be successful for higher amount");

        // Verify state is updated in DB
        Auction updated = auctionDAO.getAuctionById("auction1");
        assertNotNull(updated);
        assertEquals(1200.0, updated.getCurrentHighestBid());
        assertEquals("bidder1", updated.getCurrentHighestBidder());
        assertEquals(1, updated.getBidCount());
    }

    @Test
    public void testPlaceBidLowerOrEqualFails() {
        // Create an active running auction
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        Auction auction = new Auction("auction1", "item1", "seller1", start, end);
        auction.setCurrentHighestBid(1000.0);
        auction.setStartingPrice(1000.0);
        auction.setStatus("RUNNING");
        auctionDAO.createAuction(auction);

        // Bidder 1 bids lower amount
        boolean result1 = engine.placeBid("auction1", "bidder1", 900.0);
        assertFalse(result1, "Place bid should fail for lower amount");

        // Bidder 1 bids equal amount
        boolean result2 = engine.placeBid("auction1", "bidder1", 1000.0);
        assertFalse(result2, "Place bid should fail for equal amount");

        // Verify highest bid remained unchanged
        Auction updated = auctionDAO.getAuctionById("auction1");
        assertEquals(1000.0, updated.getCurrentHighestBid());
        assertNull(updated.getCurrentHighestBidder());
    }

    @Test
    public void testPlaceBidOnInactiveAuctionFails() {
        // Create an OPEN but not RUNNING auction
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        Auction auction = new Auction("auction1", "item1", "seller1", start, end);
        auction.setCurrentHighestBid(1000.0);
        auction.setStartingPrice(1000.0);
        auction.setStatus("OPEN");
        auctionDAO.createAuction(auction);

        // Bidder 1 places a bid
        boolean result = engine.placeBid("auction1", "bidder1", 1200.0);
        assertFalse(result, "Bidding on an OPEN (inactive) auction should fail");

        // Create a CLOSED/FINISHED auction
        auction.setStatus("CLOSED");
        auctionDAO.updateAuction(auction);

        boolean result2 = engine.placeBid("auction1", "bidder1", 1500.0);
        assertFalse(result2, "Bidding on a CLOSED auction should fail");
    }

    @Test
    public void testAntiSnipingExtension() {
        // Create a running auction expiring in 30 seconds
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now().plusSeconds(30);
        Auction auction = new Auction("auction1", "item1", "seller1", start, end);
        auction.setCurrentHighestBid(1000.0);
        auction.setStartingPrice(1000.0);
        auction.setStatus("RUNNING");
        auctionDAO.createAuction(auction);

        // Place a bid in the sniping window
        boolean result = engine.placeBid("auction1", "bidder1", 1200.0);
        assertTrue(result);

        // Verify that the auction endTime was extended
        Auction updated = auctionDAO.getAuctionById("auction1");
        assertNotNull(updated);
        // Original end was plusSeconds(30). Now it should be extended by 60 seconds, meaning it should end at end + 60s.
        // Let's assert that the updated end time is after the original end time.
        assertTrue(updated.getEndTime().isAfter(end), "Auction end time should be extended");
        
        long diffSeconds = java.time.temporal.ChronoUnit.SECONDS.between(end, updated.getEndTime());
        assertEquals(60, diffSeconds, "Auction should be extended by exactly 60 seconds");
    }
}
