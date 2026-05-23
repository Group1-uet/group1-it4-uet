package com.Auction.Common.Service;

import com.Auction.Common.Models.Auction.*;
import com.Auction.Common.Models.Item.Item;
import com.Auction.Common.Exceptions.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class AuctionService {
    private final Map<String, Auction> auctions = new ConcurrentHashMap<>();
    private static AuctionService instance;

    private AuctionService() {
        // Singleton
    }

    public static synchronized AuctionService getInstance() {
        if (instance == null) {
            instance = new AuctionService();
        }
        return instance;
    }

    /**
     * Tạo phiên đấu giá mới
     */
    public Auction createAuction(Item item, double startingPrice, Instant startTime, Instant endTime)
            throws IllegalArgumentException {
        Auction auction = new Auction(item, startingPrice, startTime, endTime);
        auctions.put(auction.getId(), auction);
        return auction;
    }

    /**
     * Lấy phiên đấu giá theo ID
     */
    public Auction getAuctionById(String auctionId) {
        return auctions.get(auctionId);
    }

    /**
     * Lấy tất cả phiên đấu giá đang chạy
     */
    public List<Auction> getActiveAuctions() {
        return auctions.values().stream()
                .filter(a -> a.getState() == AuctionStatus.RUNNING || a.getState() == AuctionStatus.OPEN)
                .collect(Collectors.toList());
    }

    /**
     * Lấy tất cả phiên đấu giá
     */
    public List<Auction> getAllAuctions() {
        return new ArrayList<>(auctions.values());
    }

    /**
     * Lấy phiên đấu giá theo người bán
     */
    public List<Auction> getAuctionsBySeller(String sellerId) {
        return auctions.values().stream()
                .filter(a -> a.getItem().getSellerId().equals(sellerId))
                .collect(Collectors.toList());
    }

    /**
     * Đặt giá cho phiên đấu giá
     */
    public BidTransaction placeBid(String auctionId, String bidderId, String bidderName, double amount)
            throws AuctionClosedException, InvalidBidException {
        Auction auction = getAuctionById(auctionId);
        if (auction == null) {
            throw new InvalidBidException("Auction not found!");
        }

        // Kiểm tra giá đấu hợp lệ
        double currentPrice = auction.getLeadingBid() != null
                ? auction.getLeadingBid().getAmount()
                : auction.getStartingPrice();

        if (amount <= currentPrice) {
            throw new InvalidBidException("Bid amount must be higher than current price: " + currentPrice);
        }

        return auction.placeBid(bidderId, bidderName, amount);
    }

    /**
     * Cập nhật trạng thái phiên đấu giá
     */
    public void refreshAllAuctions() {
        auctions.values().forEach(Auction::refreshState);
    }

    /**
     * Kết thúc phiên đấu giá
     */
    public void finishAuction(String auctionId) throws IllegalArgumentException {
        Auction auction = getAuctionById(auctionId);
        if (auction == null) {
            throw new IllegalArgumentException("Auction not found!");
        }
        auction.refreshState();
    }

    /**
     * Hủy phiên đấu giá
     */
    public void cancelAuction(String auctionId) throws IllegalArgumentException {
        Auction auction = getAuctionById(auctionId);
        if (auction == null) {
            throw new IllegalArgumentException("Auction not found!");
        }
        // Cần thêm method để set trạng thái CANCELED vào Auction.java
    }

    /**
     * Lấy lịch sử đấu giá của người dùng
     */
    public List<BidTransaction> getUserBidHistory(String userId) {
        List<BidTransaction> history = new ArrayList<>();
        for (Auction auction : auctions.values()) {
            history.addAll(auction.getBids().stream()
                    .filter(bid -> bid.getBidderId().equals(userId))
                    .collect(Collectors.toList()));
        }
        return history;
    }
}