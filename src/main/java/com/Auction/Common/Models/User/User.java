package com.Auction.Common.Models;

public abstract class User extends Entity {
    protected String name;
    protected UserRole role;

    public User(String id, String name, UserRole role) {
        super(id);
        this.name = name;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public UserRole getRole() {
        return role;
    }
}


