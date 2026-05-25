package auction.engine;

import auction.dao.AuctionDAO;
import auction.model.Auction;
import auction.network.Message;
import auction.network.MessageType;
import auction.network.GsonHelper;

import java.time.LocalDateTime;
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
        try {
        AuctionDAO dao = engine.getAuctionDAO();
        List<Auction> allAuctions = dao.getAllAuctions();
        LocalDateTime now = LocalDateTime.now();
        boolean changed = false;
        
        for (Auction auction : allAuctions) {
            String status = auction.getStatus();
            if ("OPEN".equals(status)) {
                // If now has reached or passed start time, start it
                if (!now.isBefore(auction.getStartTime())) {
                    auction.setStatus("RUNNING");
                    dao.updateAuction(auction);
                    System.out.println("[AuctionTimer] Started auction: " + auction.getAuctionId());
                    changed = true;
                }
            } else if ("RUNNING".equals(status)) {
                // If now has passed end time, close it
                if (now.isAfter(auction.getEndTime())) {
                    auction.setStatus("FINISHED");
                    dao.updateAuction(auction);
                    System.out.println("[AuctionTimer] Finished auction: " + auction.getAuctionId());
                    
                    // Deduct balance from the winner if there is one
                    String winner = auction.getCurrentHighestBidder();
                    if (winner != null && !winner.isEmpty()) {
                        auction.dao.UserDAO userDAO = new auction.dao.UserDAO();
                        auction.model.User u = userDAO.getUserById(winner);
                        if (u instanceof auction.model.Bidder) {
                            auction.model.Bidder bidder = (auction.model.Bidder) u;
                            double finalPrice = auction.getCurrentHighestBid();
                            if (bidder.getAccountBalance() >= finalPrice) {
                                bidder.deductBalance(finalPrice);
                                userDAO.updateUser(bidder);
                                auction.setStatus("PAID");
                                dao.updateAuction(auction);
                                System.out.println("[AuctionTimer] Winner " + winner + " paid " + finalPrice + " for auction " + auction.getAuctionId());
                            } else {
                                auction.setStatus("CANCELED");
                                dao.updateAuction(auction);
                                System.out.println("[AuctionTimer] Winner " + winner + " had insufficient balance, auction canceled.");
                            }
                        }
                    }
                    
                    changed = true;
                    
                    // Broadcast session closed
                    Message msg = new Message(MessageType.AUCTION_CLOSED);
                    msg.put("auctionId", auction.getAuctionId());
                    msg.put("winner", winner != null ? winner : "NONE");
                    msg.put("status", auction.getStatus());
                    NotificationManager.getInstance().broadcast(msg);
                }
            }
        }

        if (changed) {
            // Broadcast updated auctions list to all clients
            Message listUpdate = new Message(MessageType.GET_AUCTIONS_RESPONSE);
            listUpdate.put("auctions", GsonHelper.getGson().toJson(dao.getAllAuctions()));
            NotificationManager.getInstance().broadcast(listUpdate);
        }
        } catch (Exception e) {
            System.err.println("[AuctionTimer] Error in checkExpiredAuctions: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
