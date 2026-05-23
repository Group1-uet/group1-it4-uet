package auction.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Notification {
    private String notificationId;
    private String userId;
    private String auctionId;
    private String type; // BID_UPDATE, AUCTION_CLOSED, AUCTION_STARTED, OUTBID, WON, etc.
    private String message;
    private LocalDateTime timestamp;
    private boolean isRead;

    public Notification(String notificationId, String userId, String auctionId, String type, String message) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.auctionId = auctionId;
        this.type = type;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(String auctionId) {
        this.auctionId = auctionId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public void markAsRead() {
        this.isRead = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(notificationId, that.notificationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notificationId);
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId='" + notificationId + '\'' +
                ", userId='" + userId + '\'' +
                ", auctionId='" + auctionId + '\'' +
                ", type='" + type + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", isRead=" + isRead +
                '}';
    }
}

