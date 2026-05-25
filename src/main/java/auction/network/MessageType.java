package auction.network;

public enum MessageType {
    LOGIN_REQUEST,
    LOGIN_RESPONSE,
    BID_REQUEST,
    BID_RESPONSE,
    AUTO_BID_REQUEST,    // Client đăng ký Auto-Bid
    AUTO_BID_RESPONSE,   // Server xác nhận đăng ký
    AUTO_BID_CANCEL,     // Client hủy Auto-Bid
    AUCTION_EXTENDED,
    AUCTION_CLOSED,
    NEW_BID,
    NOTIFICATION,
    ERROR,
    UNKNOWN
}
