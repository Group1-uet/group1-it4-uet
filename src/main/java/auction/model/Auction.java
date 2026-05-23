package auction.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Auction {
    private String auctionId;
    private String itemId;
    private String sellerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double currentHighestBid;
    private String currentHighestBidder;
    private String status; // PENDING, ACTIVE, CLOSED, CANCELLED
    private int bidCount;

    public Auction(String auctionId, String itemId, String sellerId, LocalDateTime startTime, LocalDateTime endTime) {
        this.auctionId = auctionId;
        this.itemId = itemId;
        this.sellerId = sellerId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = "PENDING";
        this.bidCount = 0;
        this.currentHighestBidder = null;
    }

    public String getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(String auctionId) {
        this.auctionId = auctionId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public double getCurrentHighestBid() {
        return currentHighestBid;
    }

    public void setCurrentHighestBid(double currentHighestBid) {
        if (currentHighestBid < 0) {
            throw new IllegalArgumentException("Bid amount cannot be negative");
        }
        this.currentHighestBid = currentHighestBid;
    }

    public String getCurrentHighestBidder() {
        return currentHighestBidder;
    }

    public void setCurrentHighestBidder(String currentHighestBidder) {
        this.currentHighestBidder = currentHighestBidder;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (!status.matches("PENDING|ACTIVE|CLOSED|CANCELLED")) {
            throw new IllegalArgumentException("Invalid auction status");
        }
        this.status = status;
    }

    public int getBidCount() {
        return bidCount;
    }

    public void setBidCount(int bidCount) {
        this.bidCount = bidCount;
    }

    public void incrementBidCount() {
        this.bidCount++;
    }

    public boolean isActive() {
        return "ACTIVE".equals(status);
    }

    public boolean isClosed() {
        return "CLOSED".equals(status);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(endTime);
    }

    public long getRemainingSeconds() {
        return java.time.temporal.ChronoUnit.SECONDS.between(LocalDateTime.now(), endTime);
    }

    public void extendAuction(int secondsToAdd) {
        this.endTime = this.endTime.plusSeconds(secondsToAdd);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Auction auction = (Auction) o;
        return Objects.equals(auctionId, auction.auctionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(auctionId);
    }

    @Override
    public String toString() {
        return "Auction{" +
                "auctionId='" + auctionId + '\'' +
                ", itemId='" + itemId + '\'' +
                ", sellerId='" + sellerId + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", currentHighestBid=" + currentHighestBid +
                ", currentHighestBidder='" + currentHighestBidder + '\'' +
                ", status='" + status + '\'' +
                ", bidCount=" + bidCount +
                '}';
    }
}

