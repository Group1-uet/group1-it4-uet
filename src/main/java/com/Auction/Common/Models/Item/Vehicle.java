package com.Auction.Common.Models;

public class Vehicle extends Item {

    private String brand;
    private int year;

    public Vehicle(String id, String name, double price, String brand, int year) {
        super(id, name, price);
        this.brand = brand;
        this.year = year;
    }

    @Override
    public void printInfo() {
        System.out.println("Vehicle: " + name +
                " | Brand: " + brand +
                " | Year: " + year +
                " | Price: " + startingPrice);
    }
}