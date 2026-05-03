package com.Auction.Common.Models.Auction;

import com.Auction.Common.Models.Entity;
import com.Auction.Common.Models.Item.Item;
import com.Auction.Common.Exceptions.AuthenticationException;
import com.Auction.Common.Exceptions.AuctionClosedException;
import com.Auction.Common.Exceptions.InvalidBidException;


import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public class Auction extends Entity {

    private final Item item;
    private final double startingPrice;

    private final Instant startTime;
    private Instant endTime;

    private AuctionStatus status;

    private final List<BidTransaction> bids = new ArrayList<>();
    private BidTransaction leadingBid;
    private String winnerId;

    private transient ReentrantLock lock = new ReentrantLock(true);

    public Auction(Item item, double startingPrice, Instant startTime, Instant endTime) {
        super();
        this.item = Objects.requireNonNull(item, "item");

        if (startingPrice < 0) {
            throw new IllegalArgumentException("The starting price must be >= 0");
        }

        this.startingPrice = startingPrice;
        this.startTime = Objects.requireNonNull(startTime, "startTime");
        this.endTime = Objects.requireNonNull(endTime, "endTime");

        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time must be after startTime");
        }

        this.status = Instant.now().isBefore(startTime) ? AuctionStatus.OPEN : AuctionStatus.RUNNING;
    }

    private ReentrantLock lock() {
        return lock;
    }

    public Item getItem() {
        return item;
    }

    public double getStartingPrice() {
        return startingPrice;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        lock().lock();
        try {
            return endTime;
        } finally {
            lock().unlock();
        }
    }

    public AuctionStatus getState() {
        lock().lock();
        try {
            return status;
        } finally {
            lock().unlock();
        }
    }

    public String getWinnerId() {
        lock().lock();
        try {
            return winnerId;
        } finally {
            lock().unlock();
        }
    }

    public BidTransaction getLeadingBid() {
        lock().lock();
        try {
            return leadingBid;
        } finally {
            lock().unlock();
        }
    }

    public List<BidTransaction> getBids() {
        lock().lock();
        try {
            return Collections.unmodifiableList(new ArrayList<>(bids)); }
        finally {
            lock().unlock();
        }
    }

    /**
     * Bắt đầu vòng đời của 1 phiên đấu giá: Được làm mới dựa trên trạng thái hiện tại
     * Được gọi bởi Schedule tick của máy chủ
     * @return true nếu thay đổi trạng thái (thông báo cho bên theo dõi)
     */


    public boolean refreshState() {
        lock().lock();
        try {
            Instant now = Instant.now();
            if (status == AuctionStatus.OPEN && !now.isBefore(startTime)) {
                status = AuctionStatus.RUNNING;
                return true;
            }
            if (status == AuctionStatus.RUNNING && !now.isBefore(endTime)) {
                status = AuctionStatus.FINISHED;
                if (leadingBid != null) {
                    winnerId = leadingBid.getBidderId();
                } else {
                    status = AuctionStatus.CANCELED;
                }
                return true;
            }
            return false;
        } finally {
            lock().unlock();
        }
    }

    public BidTransaction placeBid(String bidderId, String bidderName, double amount)
            throws AuctionClosedException, InvalidBidException {

        Objects.requireNonNull(bidderId, "bidderId");

        lock().lock();
        try {
            // 1. Cập nhật trạng thái "lười" (Lazy refresh)
            // Đảm bảo trạng thái luôn mới nhất ngay cả khi Scheduler chưa kịp chạy
            Instant now = Instant.now();
            if (status == AuctionStatus.OPEN && !now.isBefore(startTime)) {
                status = AuctionStatus.RUNNING;
            }

            // 2. Các kiểm tra điều kiện đóng/mở cuộc đấu giá
            if (status != AuctionStatus.RUNNING) {
                throw new AuctionClosedException("Auction is " + status + ", bids not accepted.");
            }
            if (now.isBefore(startTime)) {
                throw new AuctionClosedException("Auction has not started yet.");
            }
            if (!now.isBefore(endTime)) {
                status = AuctionStatus.FINISHED;
                if (leadingBid != null) winnerId = leadingBid.getBidderId();
                else status = AuctionStatus.CANCELED;
                throw new AuctionClosedException("Auction has ended.");
            }

            // 3. Kiểm tra tính hợp lệ của người đặt giá (Business Rules)
            if (bidderId.equals(item.getSellerId())) {
                throw new InvalidBidException("Sellers cannot bid on their own item.");
            }

            if (leadingBid != null && bidderId.equals(leadingBid.getBidderId())) {
                throw new InvalidBidException("You are already the highest bidder.");
            }

            // 4. Ghi nhận giao dịch đặt giá mới
            BidTransaction tx = new BidTransaction(getId(), bidderId, bidderName, amount, now);
            bids.add(tx);
            leadingBid = tx;
            return tx;
        } finally {
            lock().unlock();
        }
    }
}