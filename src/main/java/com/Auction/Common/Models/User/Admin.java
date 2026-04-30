package com.Auction.Common.Models;

public class Admin extends User {

    public Admin(String id, String name) {
        super(id, name, UserRole.ADMIN);
    }

    public void endAuction(Auction auction) {
        auction.end();
    }
}