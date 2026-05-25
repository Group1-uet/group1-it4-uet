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

public class AutoBidManagerTest {

    private static UserDAO userDAO;
    private static ItemDAO itemDAO;
    private static AuctionDAO auctionDAO;
    private static AuctionEngine engine;
    private static AutoBidManager autoBidManager;

    private Bidder bidder1;
    private Bidder bidder2;
    private Seller seller;
    private Item item;

    @BeforeAll
    public static void setUpClass() {
        DatabaseConnection.setUrl("jdbc:sqlite:test_auction_db.sqlite");
        DatabaseConnection.initializeDatabase();

        userDAO = new UserDAO();
        itemDAO = new ItemDAO();
        auctionDAO = new AuctionDAO();
        engine = AuctionEngine.getInstance();
        autoBidManager = AutoBidManager.getInstance();
    }

    @BeforeEach
    public void setUp() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM bids;");
            stmt.execute("DELETE FROM auctions;");
            stmt.execute("DELETE FROM items;");
            stmt.execute("DELETE FROM users;");
        }

        bidder1 = new Bidder("bidder1", "bidder1_user", "password123", "bidder1@example.com", 20000.0);
        bidder2 = new Bidder("bidder2", "bidder2_user", "password123", "bidder2@example.com", 25000.0);
        seller = new Seller("seller1", "seller_user", "password123", "seller@example.com", "MyStore");

        userDAO.createUser(bidder1);
        userDAO.createUser(bidder2);
        userDAO.createUser(seller);

        autoBidManager.clear();
        item = new Electronics("item1", "Laptop", "Gaming Laptop", 1000.0, "seller1");
        itemDAO.createItem(item);
    }

    @Test
    public void testRegisterAndHasAutoBids() {
        AutoBidConfig config = new AutoBidConfig("bidder1", "auction1", 5000.0, 100.0);
        autoBidManager.register(config);

        assertTrue(autoBidManager.hasAutoBids("auction1"), "Should have auto bids registered");
        
        autoBidManager.unregister("auction1", "bidder1");
        assertFalse(autoBidManager.hasAutoBids("auction1"), "Should have no auto bids after unregistering");
    }

    @Test
    public void testAutoBidTriggerAndBiddingWar() throws InterruptedException {
        // Create an active running auction
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        Auction auction = new Auction("auction1", "item1", "seller1", start, end);
        auction.setCurrentHighestBid(1000.0);
        auction.setStartingPrice(1000.0);
        auction.setStatus("RUNNING");
        auctionDAO.createAuction(auction);

        // Register auto-bids for both bidder1 and bidder2
        // Bidder1: Max 5000, Increment 500
        AutoBidConfig config1 = new AutoBidConfig("bidder1", "auction1", 5000.0, 500.0);
        autoBidManager.register(config1);
        
        // Wait a tiny bit to make sure registeredAt is different
        Thread.sleep(20);

        // Bidder2: Max 4000, Increment 400
        AutoBidConfig config2 = new AutoBidConfig("bidder2", "auction1", 4000.0, 400.0);
        autoBidManager.register(config2);

        // Place initial bid manually by bidder2 to kickstart the auto-bidding war
        boolean manualBid = engine.placeBid("auction1", "bidder2", 1500.0);
        assertTrue(manualBid);

        // Give the async CompletableFuture tasks time to run and trigger the bidding war
        Thread.sleep(500);

        // Let's verify what happened:
        // bidder2 bid 1500 manually.
        // Auto-bid triggers bidder1 to bid 1500 + 500 = 2000.
        // Auto-bid triggers bidder2 to bid 2000 + 400 = 2400.
        // Auto-bid triggers bidder1 to bid 2400 + 500 = 2900.
        // ...
        // Bidding continues until bidder2 reaches its limit (max 4000).
        // Let's check the final price.
        Auction finalAuction = auctionDAO.getAuctionById("auction1");
        assertNotNull(finalAuction);
        assertTrue(finalAuction.getCurrentHighestBid() > 2000.0);
        // The current highest bidder should be bidder1 since their max limit (5000) is higher than bidder2's limit (4000).
        assertEquals("bidder1", finalAuction.getCurrentHighestBidder());
    }

    @Test
    public void testAutoBidCapAtMaxBid() throws InterruptedException {
        // Create an active running auction
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        Auction auction = new Auction("auction1", "item1", "seller1", start, end);
        auction.setCurrentHighestBid(1000.0);
        auction.setStartingPrice(1000.0);
        auction.setStatus("RUNNING");
        auctionDAO.createAuction(auction);

        // Bidder1 registers auto-bid: Max 1500, Increment 500
        AutoBidConfig config = new AutoBidConfig("bidder1", "auction1", 1500.0, 500.0);
        autoBidManager.register(config);

        // Bidder2 bids 1200.0 manually
        boolean manualBid = engine.placeBid("auction1", "bidder2", 1200.0);
        assertTrue(manualBid);

        // Wait for async processing
        Thread.sleep(200);

        // Bidder1's auto-bid should trigger. 1200 + 500 = 1700, but capped at Max Bid = 1500.
        // Wait, is 1500 > currentHighestBid (1200)? Yes, so they bid 1500.
        Auction finalAuction = auctionDAO.getAuctionById("auction1");
        assertEquals(1500.0, finalAuction.getCurrentHighestBid());
        assertEquals("bidder1", finalAuction.getCurrentHighestBidder());
    }
}
