package auction.engine;

import auction.dao.AuctionDAO;
import auction.model.Auction;
import auction.network.Message;
import auction.network.MessageType;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AuctionTimer {
    private final ScheduledExecutorService scheduler;
    private final AuctionEngine engine;

    public AuctionTimer() {
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.engine = AuctionEngine.getInstance();
    }

    public void start() {
        // Quét mỗi giây một lần để kiểm tra các phiên đã hết giờ
        scheduler.scheduleAtFixedRate(this::checkExpiredAuctions, 0, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        scheduler.shutdown();
    }

    private void checkExpiredAuctions() {
        AuctionDAO dao = engine.getAuctionDAO();
        List<Auction> allAuctions = dao.getAllAuctions();
        
        for (Auction auction : allAuctions) {
            if (auction.isActive() && auction.isExpired()) {
                auction.setStatus("CLOSED");
                dao.updateAuction(auction);
                
                String winner = auction.getCurrentHighestBidder();
                Message msg = new Message(MessageType.AUCTION_CLOSED);
                msg.put("auctionId", auction.getAuctionId());
                if (winner != null) {
                    msg.put("winner", winner);
                } else {
                    msg.put("winner", "NONE");
                }
                NotificationManager.getInstance().broadcast(msg);
            }
        }
    }
}
