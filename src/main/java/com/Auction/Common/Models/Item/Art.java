package com.Auction.Common.Models;

import java.time.LocalDate;

public class Art extends Item {

    private String artist;

    public Art(String id, String name, double price, String artist) {
        super(id, name, price);
        this.artist = artist;
    }

    @Override
    public void printInfo() {
        System.out.println("Art: " + name + " by " + artist + " - " + startingPrice);
    }
}