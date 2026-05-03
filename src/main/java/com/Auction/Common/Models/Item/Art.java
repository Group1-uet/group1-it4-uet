package com.Auction.Common.Models.Item;

import java.time.LocalDate;

public class Art extends Item{

    private String artist;
    private int year;

    public Art(String name, String description, String sellerId, String artist, int year) {
        super(name, description, sellerId);
        this.artist = artist == null ? "" : artist;
        this.year = year;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist == null ? "" : artist;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public String getCategory() {
        return "Art";
    }
}