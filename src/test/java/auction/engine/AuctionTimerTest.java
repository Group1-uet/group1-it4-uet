package auction.engine;

import auction.db.DatabaseConnection;
import auction.dao.AuctionDAO;
import auction.dao.ItemDAO;
import auction.dao.UserDAO;
import auction.dao.BidTransactionDAO;
import auction.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class AuctionTimerTest {

    private static UserDAO userDAO;
    private static ItemDAO itemDAO;
    private static AuctionDAO auctionDAO;
    private static BidTransactionDAO bidDAO;
    private static AuctionTimer timer;

    private Bidder bidder;
    private Seller seller;
    private Item item;

    @BeforeAll
    public static void setUpClass() {
        DatabaseConnection.setUrl("jdbc:sqlite:test_auction_db.sqlite");
        DatabaseConnection.initializeDatabase();

        userDAO = new UserDAO();
        itemDAO = new ItemDAO();
        auctionDAO = new AuctionDAO();
        bidDAO = new BidTransactionDAO();
        timer = new AuctionTimer();
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

        bidder = new Bidder("bidder1", "bidder_user", "password", "bidder@example.com", 5000.0);
        seller = new Seller("seller1", "seller_user", "password", "seller@example.com");

        userDAO.createUser(bidder);
        userDAO.createUser(seller);

        item = new Electronics("item1", "Laptop", "Laptop Description", 1000.0, "seller1");
        itemDAO.createItem(item);
    }

    private void invokeCheckExpiredAuctions() throws Exception {
        Method method = AuctionTimer.class.getDeclaredMethod("checkExpiredAuctions");
        method.setAccessible(true);
        method.invoke(timer);
    }

    @Test
    public void testTransitionOpenToRunning() throws Exception {
        // Create an auction with start time in the past, status OPEN
        LocalDateTime start = LocalDateTime.now().minusMinutes(5);
        LocalDateTime end = LocalDateTime.now().plusMinutes(30);
        Auction auction = new Auction("auction1", "item1", "seller1", start, end);
        auction.setStartingPrice(1000.0);
        auction.setCurrentHighestBid(1000.0);
        auction.setStatus("OPEN");
        auctionDAO.createAuction(auction);

        // Run timer inspection
        invokeCheckExpiredAuctions();

        // Check if status is now RUNNING
        Auction updated = auctionDAO.getAuctionById("auction1");
        assertNotNull(updated);
        assertEquals("RUNNING", updated.getStatus());
    }

    @Test
    public void testTransitionRunningToFinishedWithSuccessfulPayment() throws Exception {
        // Create an auction with end time in the past, status RUNNING
        LocalDateTime start = LocalDateTime.now().minusHours(2);
        LocalDateTime end = LocalDateTime.now().minusMinutes(5);
        Auction auction = new Auction("auction1", "item1", "seller1", start, end);
        auction.setStartingPrice(1000.0);
        auction.setCurrentHighestBid(3000.0); // bidder1 has 5000.0
        auction.setCurrentHighestBidder("bidder1");
        auction.setStatus("RUNNING");
        auctionDAO.createAuction(auction);

        // Create the bid transaction in the database
        BidTransaction bid = new BidTransaction("bid1", "auction1", "bidder1", 3000.0);
        bid.setTimestamp(LocalDateTime.now().minusMinutes(10));
        bidDAO.createBidTransaction(bid);

        // Run timer inspection
        invokeCheckExpiredAuctions();

        // Check if status changed to PAID and balance was deducted
        Auction updated = auctionDAO.getAuctionById("auction1");
        assertNotNull(updated);
        assertEquals("PAID", updated.getStatus());

        Bidder updatedBidder = (Bidder) userDAO.getUserById("bidder1");
        assertNotNull(updatedBidder);
        // Original balance: 5000. Price: 3000. Expected: 2000.
        assertEquals(2000.0, updatedBidder.getAccountBalance());
    }

    @Test
    public void testTransitionRunningToFinishedWithInsufficientBalance() throws Exception {
        // Create an auction with end time in the past, status RUNNING
        LocalDateTime start = LocalDateTime.now().minusHours(2);
        LocalDateTime end = LocalDateTime.now().minusMinutes(5);
        Auction auction = new Auction("auction1", "item1", "seller1", start, end);
        auction.setStartingPrice(1000.0);
        auction.setCurrentHighestBid(6000.0); // bidder1 only has 5000.0
        auction.setCurrentHighestBidder("bidder1");
        auction.setStatus("RUNNING");
        auctionDAO.createAuction(auction);

        // Create the bid transaction in the database
        BidTransaction bid = new BidTransaction("bid1", "auction1", "bidder1", 6000.0);
        bid.setTimestamp(LocalDateTime.now().minusMinutes(10));
        bidDAO.createBidTransaction(bid);

        // Run timer inspection
        invokeCheckExpiredAuctions();

        // Check if status changed to CANCELED and balance was NOT deducted
        Auction updated = auctionDAO.getAuctionById("auction1");
        assertNotNull(updated);
        assertEquals("CANCELED", updated.getStatus());

        Bidder updatedBidder = (Bidder) userDAO.getUserById("bidder1");
        assertNotNull(updatedBidder);
        // Balance remains 5000
        assertEquals(5000.0, updatedBidder.getAccountBalance());
    }
}
