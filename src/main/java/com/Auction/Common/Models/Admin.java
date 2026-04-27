package com.Auction.Common.Models;

public class Admin extends User{
    public Admin(String displayName, String username, String password) {
        super(displayName, username, password);
    }

    @Override
    public UserRole getRole() {
        return UserRole.ADMIN;
    }


    @Override
    public void printInfo() {

    }
}
