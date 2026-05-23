package com.Auction.Common.Service;

import com.Auction.Common.Models.Auction.Auction;
import com.Auction.Common.Models.Auction.AuctionStatus;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AuctionScheduler {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static AuctionScheduler instance;
    private final AuctionService auctionService;

    private AuctionScheduler() {
        this.auctionService = AuctionService.getInstance();
        startScheduler();
    }

    public static synchronized AuctionScheduler getInstance() {
        if (instance == null) {
            instance = new AuctionScheduler();
        }
        return instance;
    }

    /**
     * Bắt đầu scheduler để tự động cập nhật trạng thái phiên đấu giá
     */
    private void startScheduler() {
        scheduler.scheduleAtFixedRate(this::refreshAllAuctions, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * Cập nhật trạng thái tất cả phiên đấu giá
     */
    private void refreshAllAuctions() {
        List<Auction> auctions = auctionService.getAllAuctions();
        for (Auction auction : auctions) {
            if (auction.refreshState()) {
                // Trạng thái thay đổi - có thể gửi thông báo
                notifyAuctionStateChanged(auction);
            }
        }
    }

    /**
     * Gửi thông báo khi trạng thái phiên đấu giá thay đổi
     */
    private void notifyAuctionStateChanged(Auction auction) {
        System.out.println("Auction " + auction.getId() + " state changed to: " + auction.getState());

        // Nếu phiên kết thúc
        if (auction.getState() == AuctionStatus.FINISHED) {
            if (auction.getWinnerId() != null) {
                System.out.println("Winner: " + auction.getWinnerId());
            } else {
                System.out.println("Auction finished with no bids");
            }
        }
    }

    /**
     * Dừng scheduler
     */
    public void shutdown() {
        scheduler.shutdown();
    }
}