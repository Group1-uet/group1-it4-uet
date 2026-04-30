package com.Auction.Common.Models;

import com.Auction.Common.Models.Item.Item;

import java.util.ArrayList;
import java.util.List;

public class Auction {

    private Item item;
    private double currentPrice;
    private Bidder highestBidder;
    private AuctionStatus status;

    private List<Observer> observers = new ArrayList<>();
    private List<BidTransaction> history = new ArrayList<>();

    public Auction(Item item) {
        this.item = item;
        this.currentPrice = item.getStartingPrice();
        this.status = AuctionStatus.OPEN;
    }

    public void addObserver(Observer o) {
        observers.add(o);
    }

    private void notifyObservers(String msg) {
        for (Observer o : observers) {
            o.update(msg);
        }
    }

    public void start() {
        status = AuctionStatus.RUNNING;
        notifyObservers("Auction started!");
    }

    public void end() {
        status = AuctionStatus.FINISHED;
        notifyObservers("Auction ended. Winner: " +
                (highestBidder != null ? highestBidder.getName() : "None"));
    }

    public synchronized void placeBid(Bidder bidder, double amount)
            throws InvalidBidException, AuctionClosedException {

        if (status != AuctionStatus.RUNNING) {
            throw new AuctionClosedException("Auction is not running!");
        }

        if (amount <= currentPrice) {
            throw new InvalidBidException("Bid must be higher than current price!");
        }

        currentPrice = amount;
        highestBidder = bidder;

        BidTransaction bid = new BidTransaction(bidder, amount);
        history.add(bid);

        notifyObservers(bid.toString());
    }

    public void printHistory() {
        history.forEach(System.out::println);
    }
}