package com.Auction.Common.Models;

public abstract class User extends Entity{
    private String displayName;
    private String username;
    private String password;

    public User(String displayName, String username, String password) {
        super();
        this.displayName = displayName;
        this.username = username;
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public abstract UserRole getRole();

}



