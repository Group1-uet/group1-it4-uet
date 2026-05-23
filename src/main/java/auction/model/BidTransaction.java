package auction.model;

import java.time.LocalDateTime;

public class BidTransaction {
    private String bidderId;
    private double bidAmount;
    private LocalDateTime timestamp;

    public BidTransaction(String bidderId, double bidAmount) {
        this.bidderId = bidderId;
        this.bidAmount = bidAmount;
        this.timestamp = LocalDateTime.now();
    }
}