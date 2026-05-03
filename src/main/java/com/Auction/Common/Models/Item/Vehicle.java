package com.Auction.Common.Models.Item;

import java.time.LocalDate;

public class Vehicle extends Item{
    private String model;
    private int year;

    public Vehicle(String name, String description, String sellerId, String model, int year) {
        super(name, description, sellerId);
        this.model = model == null ? "" : model;
        this.year = year;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model == null ? "" : model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public String getCategory() {
        return "Vehicle";
    }

}
