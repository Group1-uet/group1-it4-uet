package auction.engine;

import auction.dao.AuctionDAO;
import auction.dao.BidTransactionDAO;
import auction.model.Auction;
import auction.model.BidTransaction;
import auction.network.Message;
import auction.network.MessageType;

import java.util.UUID;

public class AuctionEngine {
    private static AuctionEngine instance;
    private final AuctionDAO auctionDAO;
    private final BidTransactionDAO bidDAO;

    private AuctionEngine() {
        this.auctionDAO = new AuctionDAO();
        this.bidDAO = new BidTransactionDAO();
    }

    public static synchronized AuctionEngine getInstance() {
        if (instance == null) {
            instance = new AuctionEngine();
        }
        return instance;
    }

    /**
     * Xử lý luồng đặt giá. Từ khoá synchronized đảm bảo an toàn đa luồng.
     * Khi nhiều Client cùng đặt giá, hệ thống sẽ xử lý tuần tự để tránh Lost
     * Update.
     */
    public synchronized boolean placeBid(String auctionId, String bidderId, double amount) {
        Auction auction = auctionDAO.getAuctionById(auctionId);

        if (auction == null) {
            System.out.println("Phiên đấu giá không tồn tại: " + auctionId);
            return false;
        }

        if (!auction.isActive()) {
            System.out.println("Phiên đấu giá đã kết thúc hoặc chưa bắt đầu: " + auctionId);
            return false;
        }

        if (auction.isExpired()) {
            auction.setStatus("CLOSED");
            auctionDAO.updateAuction(auction);

            Message closedMsg = new Message(MessageType.AUCTION_CLOSED);
            closedMsg.put("auctionId", auctionId);
            closedMsg.put("winner", auction.getCurrentHighestBidder());
            NotificationManager.getInstance().broadcast(closedMsg);
            return false;
        }

        // Kiểm tra giá đặt phải lớn hơn giá hiện tại
        if (amount <= auction.getCurrentHighestBid()) {
            return false;
        }

        // 1. Cập nhật Auction
        auction.setCurrentHighestBid(amount);
        auction.setCurrentHighestBidder(bidderId);
        auction.incrementBidCount();

        // 2. Anti-sniping Algorithm (Yêu cầu 3.2.3)
        // Nếu có bid trong 60 giây cuối, tự động cộng thêm 60 giây
        long remaining = auction.getRemainingSeconds();
        if (remaining > 0 && remaining <= 60) {
            auction.extendAuction(60);
            Message extendMsg = new Message(MessageType.AUCTION_EXTENDED);
            extendMsg.put("auctionId", auctionId);
            extendMsg.put("newEndTime", auction.getEndTime().toString());
            NotificationManager.getInstance().broadcast(extendMsg);
        }

        // Cập nhật DB
        auctionDAO.updateAuction(auction);

        // 3. Ghi lại Transaction
        String transactionId = UUID.randomUUID().toString();
        BidTransaction bid = new BidTransaction(transactionId, auctionId, bidderId, amount);
        bid.setStatus("ACCEPTED");
        bidDAO.createBidTransaction(bid);

        // 4. Realtime Update cho toàn bộ Client đang theo dõi
        Message newBidMsg = new Message(MessageType.NEW_BID);
        newBidMsg.put("auctionId", auctionId);
        newBidMsg.put("bidderId", bidderId);
        newBidMsg.put("amount", String.valueOf(amount));
        NotificationManager.getInstance().broadcast(newBidMsg);

        // 5. Kích hoạt Auto-Bid cho các người dùng đã đăng ký
        // Chạy bất đồng bộ (Asynchronous) để bẻ gãy đệ quy và tránh StackOverflowError
        java.util.concurrent.CompletableFuture.runAsync(() -> {
            AutoBidManager.getInstance().triggerAutoBids(auctionId);
        });

        return true;
    }

    public AuctionDAO getAuctionDAO() {
        return auctionDAO;
    }
}
