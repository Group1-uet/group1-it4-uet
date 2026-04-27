package com.Auction.Common.Models;

import java.util.ArrayList;
import java.util.List;

public class Seller extends User{
    private double earnings;

    public Seller(String displayName, String username, String password) {
        super(displayName, username, password);
        this.earnings = 0.0;
    }

    public double getEarnings() {
        return earnings;
    }

    public void addEarnings(double amount) {
        this.earnings += amount;
    }

    @Override
    public UserRole getRole() {
        return UserRole.SELLER;
    }


    @Override
    public void printInfo() {

    }
}
