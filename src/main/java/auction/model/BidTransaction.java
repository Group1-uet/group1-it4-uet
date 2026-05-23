package auction.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class BidTransaction {
    private String transactionId;
    private String auctionId;
    private String bidderId;
    private double bidAmount;
    private LocalDateTime timestamp;
    private String status; // PENDING, ACCEPTED, REJECTED

    public BidTransaction(String transactionId, String auctionId, String bidderId, double bidAmount) {
        this.transactionId = transactionId;
        this.auctionId = auctionId;
        this.bidderId = bidderId;
        this.bidAmount = bidAmount;
        this.timestamp = LocalDateTime.now();
        this.status = "PENDING";
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(String auctionId) {
        this.auctionId = auctionId;
    }

    public String getBidderId() {
        return bidderId;
    }

    public void setBidderId(String bidderId) {
        this.bidderId = bidderId;
    }

    public double getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(double bidAmount) {
        if (bidAmount < 0) {
            throw new IllegalArgumentException("Bid amount cannot be negative");
        }
        this.bidAmount = bidAmount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (!status.matches("PENDING|ACCEPTED|REJECTED")) {
            throw new IllegalArgumentException("Invalid status");
        }
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BidTransaction that = (BidTransaction) o;
        return Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }

    @Override
    public String toString() {
        return "BidTransaction{" +
                "transactionId='" + transactionId + '\'' +
                ", auctionId='" + auctionId + '\'' +
                ", bidderId='" + bidderId + '\'' +
                ", bidAmount=" + bidAmount +
                ", timestamp=" + timestamp +
                ", status='" + status + '\'' +
                '}';
    }
}