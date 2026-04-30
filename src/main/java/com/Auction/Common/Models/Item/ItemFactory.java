package com.Auction.Common.Models.Item;

import java.util.Map;
import java.util.Objects;

public class ItemFactory {
    public static Item create(String category, String name, String description,
                              String sellerId, Map<String, String> props) {

        Objects.requireNonNull(category, "category");
        Map<String, String> p = props == null ? Map.of() : props;

        return switch (category.trim().toLowerCase()) {
            case "electronics" -> new Electronics(
                    name, description, sellerId,
                    p.getOrDefault("brand", ""),
                    parseInt(p.get("warrantyMonths"), 0));
            case "art" -> new Art(
                    name, description, sellerId,
                    p.getOrDefault("artist", ""),
                    parseInt(p.get("year"), 0));
            case "vehicle" -> new Vehicle(
                    name, description, sellerId,
                    p.getOrDefault("make", ""),
                    p.getOrDefault("model", ""),
                    parseInt(p.get("year"), 0));
            default -> throw new IllegalArgumentException("Unknown item category: " + category);
        };
    }

    public static String[] supportedCategories() {
        return new String[] {"Electronics", "Art", "Vehicle"};
    }

    private static int parseInt(String s, int fallback) {
        if (s == null || s.isBlank())
            return fallback;
        try {
            return Integer.parseInt(s.trim());
        }
        catch (NumberFormatException e) {
            return fallback;
        }
    }
}

