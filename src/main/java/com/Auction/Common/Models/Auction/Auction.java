package com.Auction.Common.Models.Auction;

import com.Auction.Common.Models.Entity;
import com.Auction.Common.Models.Item.Item;

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

        this.startingPrice = startingPrice;;
        this.startTime = Objects.requireNonNull(startTime, "startTime");
        this.endTime = Objects.requireNonNull(endTime, "endTime");

        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time must be after startTime");
        }

        this.status = Instant.now().isBefore(startTime) ? AuctionStatus.OPEN : AuctionStatus.RUNNING;
    }

    private ReentrantLock lock() {
        // lock is transient — recreate after deserialization on the server side.
        // For client-side snapshots we never call placeBid, so this is fine.
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
        try { return Collections.unmodifiableList(new ArrayList<>(bids)); }
        finally { lock().unlock(); }
    }
}
