package com.Auction.Common.Models;

import java.time.Instant;

import java.time.LocalDateTime;

public class BidTransaction {
    private Bidder bidder;
    private double amount;
    private LocalDateTime time;

    public BidTransaction(Bidder bidder, double amount) {
        this.bidder = bidder;
        this.amount = amount;
        this.time = LocalDateTime.now();
    }

    public String toString() {
        return bidder.getName() + " bid " + amount + " at " + time;
    }
}