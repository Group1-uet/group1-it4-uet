package com.Auction.Common.Models;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public abstract class Entity implements Serializable {

    private static final long serialVersionUID = 1L;
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

    public abstract void printInfo();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Entity entity)) {
            return false;
        }
        return id.equals(entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
