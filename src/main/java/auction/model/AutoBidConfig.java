package auction.model;

/**
 * Lưu cấu hình Auto-Bid của một người dùng cho một phiên đấu giá.
 * Sử dụng cùng với PriorityQueue trong AutoBidManager để xác định thứ tự ưu tiên.
 */
public class AutoBidConfig implements Comparable<AutoBidConfig> {
    private final String bidderId;
    private final String auctionId;
    private final double maxBid;     // Giá tối đa người dùng chấp nhận trả
    private final double increment;  // Bước giá mỗi lần tự động bid
    private final long registeredAt; // Thời điểm đăng ký (dùng để ưu tiên đăng ký trước)

    public AutoBidConfig(String bidderId, String auctionId, double maxBid, double increment) {
        this.bidderId = bidderId;
        this.auctionId = auctionId;
        this.maxBid = maxBid;
        this.increment = increment;
        this.registeredAt = System.currentTimeMillis();
    }

    public String getBidderId() { return bidderId; }
    public String getAuctionId() { return auctionId; }
    public double getMaxBid() { return maxBid; }
    public double getIncrement() { return increment; }
    public long getRegisteredAt() { return registeredAt; }

    /**
     * Ưu tiên theo thứ tự đăng ký (đăng ký sớm hơn → ưu tiên cao hơn khi cùng mức giá).
     */
    @Override
    public int compareTo(AutoBidConfig other) {
        return Long.compare(this.registeredAt, other.registeredAt);
    }
}
