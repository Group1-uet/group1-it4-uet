package com.Auction.Common.Models;

import java.time.LocalDate;

public class Electronics extends Item {

    public Electronics(String id, String name, double price) {
        super(id, name, price);
    }

    @Override
    public void printInfo() {
        System.out.println("Electronics: " + name + " - " + startingPrice);
    }
}