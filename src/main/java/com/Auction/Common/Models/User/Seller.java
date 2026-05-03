package com.Auction.Common.Models;

import com.Auction.Common.Models.Item.Item;

public class Seller extends User {

    public Seller(String id, String name) {
        super(id, name, UserRole.SELLER);
    }

    public Item createItem(String type, String id, String name, double price) {
        try {
            return ItemFactory.createItem(type, id, name, price);
        } catch (IllegalArgumentException e) {
            System.out.println("Create item failed: " + e.getMessage());
            return null;
        }
    }
}