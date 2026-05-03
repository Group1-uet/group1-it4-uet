package com.Auction.Common.Models.User;

public class Seller extends User{
    private double earnings;

    public Seller(String displayName, String username, String password) {
        super(displayName, username, password);
        this.earnings = 0.0;
    }

    @Override
    public UserRole getRole() {
        return UserRole.SELLER;
    }

    public double getEarnings() {
        return earnings;
    }

    public void addEarnings(double amount) {
        this.earnings += amount;
    }
}
