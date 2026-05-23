package auction.model;

public class Electronics extends Item {
    private int warrantyMonths;

    public Electronics(String id, String name, String description, double startingPrice, int warrantyMonths) {
        super(id, name, description, startingPrice);
        this.warrantyMonths = warrantyMonths;
    }
    @Override
    public void printInfo() {
        System.out.println("--- Hàng Điện Tử ---");
        System.out.println("Tên: " + this.name);
        System.out.println("Bảo hành: " + this.warrantyMonths + " tháng");
        System.out.println("Giá hiện tại: $" + this.currentHighestPrice);
    }
}