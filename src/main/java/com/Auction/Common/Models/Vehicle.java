package com.Auction.Common.Models;

import java.time.LocalDate;

public class Vehicle extends Items{
    public Vehicle(String id) {
        super(id);
    }

    public Vehicle(String id, String name) {
        super(id, name);
    }

    public Vehicle(String id, String name, String descrpition, double startingPrice, double currentPrice, LocalDate timeStart, LocalDate timeEnd) {
        super(id, name, descrpition, startingPrice, currentPrice, timeStart, timeEnd);
    }

    @Override
    public void printInfo() {

    }

}
