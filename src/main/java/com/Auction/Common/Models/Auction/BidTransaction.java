package com.Auction.Common.Models.Auction;

import com.Auction.Common.Models.Entity;

import java.time.Instant;
import java.util.Objects;

public class BidTransaction extends Entity {
    private final String auctionId;
    private final String bidderId;
    private final String bidderName;
    private final double amount;

    public BidTransaction(String auctionId, String bidderId, String bidderName,
                          double amount, Instant timestamp) {
        super();
        this.auctionId = Objects.requireNonNull(auctionId, "auctionId");
        this.bidderId = Objects.requireNonNull(bidderId, "bidderId");
        this.bidderName = bidderName == null ? bidderId : bidderName;
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be > 0");
        }
        this.amount = amount;
    }

    public String getAuctionId() {
        return auctionId;
    }

    public String getBidderId() {
        return bidderId;
    }

    public String getBidderName() {
        return bidderName;
    }

    public double getAmount() {
        return amount;
    }

}
