package com.Auction.Common.Models;

public abstract class Item extends Entity {
    protected String name;
    protected double startingPrice;

    public Item(String id, String name, double startingPrice) {
        super(id);
        this.name = name;
        this.startingPrice = startingPrice;
    }

    public double getStartingPrice() {
        return startingPrice;
    }

    public abstract void printInfo();
}