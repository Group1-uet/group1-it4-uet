package com.Auction.Common.Models;

public class Bidder extends User implements Observer {

    public Bidder(String id, String name) {
        super(id, name, UserRole.BIDDER);
    }

    public void placeBid(Auction auction, double amount) {
        try {
            auction.placeBid(this, amount);
        } catch (Exception e) {
            System.out.println(name + " failed: " + e.getMessage());
        }
    }

    @Override
    public void update(String message) {
        System.out.println("[Realtime] " + name + ": " + message);
    }


}