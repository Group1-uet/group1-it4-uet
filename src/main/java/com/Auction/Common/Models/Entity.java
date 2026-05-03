package com.Auction.Common.Models;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public abstract class Entity {

    private final String id;

    public Entity() {
        this.id = UUID.randomUUID().toString();
    }

    public Entity(String id) {
        this.id = Objects.requireNonNull(id, "id");
    }

    public String getId() {
        return id;
    }

    // public abstract void printInfo();
}
