package com.Auction.Common.Models.Item;

import com.Auction.Common.Models.Entity;

import java.util.Objects;

public abstract class Item extends Entity {

    private String name;
    private String description;
    private final String sellerId;

    public Item(String name, String description, String sellerId) {
        super();
        this.name = Objects.requireNonNull(name, "name");
        this.description = description == null ? "" : description;
        this.sellerId = Objects.requireNonNull(sellerId, "sellerId");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? "" : description;
    }

    public String getSellerId() {
        return sellerId;
    }

    public abstract String getCategory();


