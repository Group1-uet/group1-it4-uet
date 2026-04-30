package com.Auction.Common.Models.User;

public class Bidder extends User{
    private double balance;

    public Bidder(String displayName, String username, String password) {
        super(displayName, username, password);
        this.balance = 0.0;
    }

    @Override
    public UserRole getRole() {
        return UserRole.BIDDER;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean canAfford(double amount) {
        return balance >= amount;
    }

    public void deductBalance(double amount) {
        if (canAfford(amount)) {
            this.balance -= amount;
        }
    }

}
