package auction.engine;

import auction.model.AutoBidConfig;

import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Quản lý tất cả cấu hình Auto-Bid.
 * Mỗi phiên đấu giá có một PriorityQueue riêng, sắp xếp theo thứ tự đăng ký
 * (FIFO).
 */
public class AutoBidManager {
    private static AutoBidManager instance;

    // Key: auctionId → Value: PriorityQueue các AutoBidConfig
    private final Map<String, PriorityQueue<AutoBidConfig>> autoBidMap = new ConcurrentHashMap<>();

    private AutoBidManager() {
    }

    public static synchronized AutoBidManager getInstance() {
        if (instance == null) {
            instance = new AutoBidManager();
        }
        return instance;
    }

    /**
     * Đăng ký cấu hình Auto-Bid cho một người dùng trong một phiên đấu giá.
     * Nếu người dùng đã đăng ký trước đó, cấu hình cũ sẽ bị xóa và thay bằng cấu
     * hình mới.
     */
    public synchronized void register(AutoBidConfig config) {
        PriorityQueue<AutoBidConfig> queue = autoBidMap.computeIfAbsent(
                config.getAuctionId(), k -> new PriorityQueue<>());

        // Xóa cấu hình cũ của cùng bidder nếu có
        queue.removeIf(c -> c.getBidderId().equals(config.getBidderId()));
        queue.add(config);
        System.out.println("[AutoBid] Registered: " + config.getBidderId()
                + " | maxBid=" + config.getMaxBid()
                + " | increment=" + config.getIncrement());
    }

    /**
     * Hủy đăng ký Auto-Bid của một người dùng trong một phiên.
     */
    public synchronized void unregister(String auctionId, String bidderId) {
        PriorityQueue<AutoBidConfig> queue = autoBidMap.get(auctionId);
        if (queue != null) {
            queue.removeIf(c -> c.getBidderId().equals(bidderId));
        }
    }

    /**
     * Sau mỗi bid thành công, kích hoạt Auto-Bid cho tất cả người đã đăng ký.
     * Logic:
     * 1. Lấy danh sách Auto-Bid configs còn đủ điều kiện (maxBid >
     * currentHighestBid)
     * và không phải người đang dẫn đầu (tránh tự bid lại chính mình).
     * 2. Sắp xếp theo thứ tự đăng ký (PriorityQueue).
     * 3. Người đăng ký sớm nhất và còn đủ tiền → tự động bid.
     * 4. Gọi lại AuctionEngine.placeBid() để xử lý và thông báo.
     *
     * @param auctionId            Phiên đấu giá
     * @param currentHighestBid    Giá cao nhất hiện tại
     * @param currentHighestBidder Người đang dẫn đầu
     */
    public synchronized void triggerAutoBids(String auctionId) {
        PriorityQueue<AutoBidConfig> queue = autoBidMap.get(auctionId);
        if (queue == null || queue.isEmpty())
            return;

        // Fetch latest auction details from DB
        auction.model.Auction auctionObj = AuctionEngine.getInstance().getAuctionDAO().getAuctionById(auctionId);
        if (auctionObj == null || !auctionObj.isActive() || auctionObj.isExpired())
            return;

        double currentHighestBid = auctionObj.getCurrentHighestBid();
        String currentHighestBidder = auctionObj.getCurrentHighestBidder();

        // Lấy danh sách eligible (không phải người đang dẫn đầu và maxBid > currentHighestBid)
        List<AutoBidConfig> eligible = queue.stream()
                .filter(c -> !c.getBidderId().equals(currentHighestBidder))
                .filter(c -> c.getMaxBid() > currentHighestBid)
                .sorted()
                .collect(Collectors.toList());

        for (AutoBidConfig config : eligible) {
            // Check balance of this auto-bidder
            auction.dao.UserDAO userDAO = new auction.dao.UserDAO();
            auction.model.User u = userDAO.getUserById(config.getBidderId());
            if (u instanceof auction.model.Bidder) {
                auction.model.Bidder bidder = (auction.model.Bidder) u;
                double nextBid = currentHighestBid + config.getIncrement();
                if (nextBid > config.getMaxBid()) {
                    nextBid = config.getMaxBid(); // Không vượt quá maxBid
                }

                if (!bidder.hasEnoughBalance(nextBid)) {
                    System.out.println("[AutoBid] Bidder " + config.getBidderId() + " has insufficient balance for auto-bid. Skipping.");
                    continue;
                }

                // Gọi AuctionEngine để đặt giá (đã synchronized bên trong)
                boolean success = AuctionEngine.getInstance().placeBid(auctionId, config.getBidderId(), nextBid);
                if (success) {
                    System.out.println("[AutoBid] Auto-bid placed by: " + config.getBidderId() + " | amount=" + nextBid);
                    break;
                }
            }
        }
    }

    public boolean hasAutoBids(String auctionId) {
        PriorityQueue<AutoBidConfig> queue = autoBidMap.get(auctionId);
        return queue != null && !queue.isEmpty();
    }
}
