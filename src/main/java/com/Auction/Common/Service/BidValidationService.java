package com.Auction.Common.Service;

import com.Auction.Common.Models.Auction.*;
import com.Auction.Common.Exceptions.InvalidBidException;

public class BidValidationService {
    private static BidValidationService instance;

    private BidValidationService() {
        // Singleton
    }

    public static synchronized BidValidationService getInstance() {
        if (instance == null) {
            instance = new BidValidationService();
        }
        return instance;
    }

    /**
     * Kiểm tra giá đấu hợp lệ
     */
    public void validateBid(Auction auction, String bidderId, double amount)
            throws InvalidBidException {
        // 1. Kiểm tra phiên đấu giá mở không
        if (auction.getState() != AuctionStatus.RUNNING) {
            throw new InvalidBidException("Auction is not running!");
        }

        // 2. Kiểm tra giá đấu > 0
        if (amount <= 0) {
            throw new InvalidBidException("Bid amount must be greater than 0!");
        }

        // 3. Kiểm tra giá đấu > giá hiện tại
        double currentPrice = auction.getLeadingBid() != null
                ? auction.getLeadingBid().getAmount()
                : auction.getStartingPrice();

        if (amount <= currentPrice) {
            throw new InvalidBidException("Bid amount must be higher than current price: " + currentPrice);
        }

        // 4. Kiểm tra người dùng không phải người bán
        if (bidderId.equals(auction.getItem().getSellerId())) {
            throw new InvalidBidException("Sellers cannot bid on their own item!");
        }

        // 5. Kiểm tra người dùng không phải là người dẫn hiện tại
        if (auction.getLeadingBid() != null && bidderId.equals(auction.getLeadingBid().getBidderId())) {
            throw new InvalidBidException("You are already the highest bidder!");
        }
    }

    /**
     * Kiểm tra bước giá tối thiểu
     */
    public boolean isValidMinimumIncrement(Auction auction, double newBidAmount) {
        BidTransaction leadingBid = auction.getLeadingBid();
        if (leadingBid == null) {
            return newBidAmount >= auction.getStartingPrice();
        }
        // Có thể set bước giá tối thiểu, ví dụ: $5
        return newBidAmount > leadingBid.getAmount();
    }
}