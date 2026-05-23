package auction.model;

import java.util.Objects;

public abstract class Item extends Entity {
    protected String name;
    protected String description;
    protected double startingPrice;
    protected double currentHighestPrice;
    protected String category;
    protected String sellerId;

    public Item(String id, String name, String description, double startingPrice, String category, String sellerId) {
        super(id);
        this.name = name;
        this.description = description;
        this.startingPrice = startingPrice;
        this.currentHighestPrice = startingPrice;
        this.category = category;
        this.sellerId = sellerId;
    }

    public abstract void printInfo();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(double startingPrice) {
        if (startingPrice < 0) {
            throw new IllegalArgumentException("Starting price cannot be negative");
        }
        this.startingPrice = startingPrice;
    }

    public double getCurrentHighestPrice() {
        return currentHighestPrice;
    }

    public void setCurrentHighestPrice(double currentHighestPrice) {
        if (currentHighestPrice < this.startingPrice) {
            throw new IllegalArgumentException("Current price cannot be less than starting price");
        }
        this.currentHighestPrice = currentHighestPrice;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Item item = (Item) o;
        return Objects.equals(name, item.name) &&
                Objects.equals(sellerId, item.sellerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, sellerId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", startingPrice=" + startingPrice +
                ", currentHighestPrice=" + currentHighestPrice +
                ", category='" + category + '\'' +
                ", sellerId='" + sellerId + '\'' +
                '}';
    }
}