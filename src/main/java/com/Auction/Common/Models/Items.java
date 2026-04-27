package com.Auction.Common.Models;

import java.time.LocalDate;

public abstract class Items extends Entity{
    private String name;
    private String descrpition;
    private double startingPrice;
    private double currentPrice;
    private LocalDate timeStart;
    private LocalDate timeEnd;

    public Items(String id) {
        super(id);
    }

    public Items(String id, String name) {
        super(id);
        this.name = name;
    }

    public Items(String id, String name, String descrpition, double startingPrice, double currentPrice, LocalDate timeStart, LocalDate timeEnd) {
        super(id);
        this.name = name;
        this.descrpition = descrpition;
        this.startingPrice = startingPrice;
        this.currentPrice = startingPrice;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public double getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(double startingPrice) {
        this.startingPrice = startingPrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }
}
