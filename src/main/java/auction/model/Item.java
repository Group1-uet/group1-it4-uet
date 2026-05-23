package auction.model;

public abstract class Item extends Entity {
    protected String name;
    protected String description;
    protected double startingPrice;
    protected double currentHighestPrice;

    public Item(String id, String name, String description, double startingPrice) {
        super(id);
        this.name = name;
        this.description = description;
        this.startingPrice = startingPrice;
        this.currentHighestPrice = startingPrice; // Ban đầu giá cao nhất là giá khởi điểm
    }

    public abstract void printInfo();

    public double getCurrentHighestPrice() { return currentHighestPrice; }
    public void setCurrentHighestPrice(double currentHighestPrice) { this.currentHighestPrice = currentHighestPrice; }
}