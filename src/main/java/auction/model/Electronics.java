package auction.model;

import java.util.Objects;

public class Electronics extends Item {
    private int warrantyMonths;
    private String brand;
    private String model;

    public Electronics(String id, String name, String description, double startingPrice,
                      int warrantyMonths, String brand, String model, String sellerId) {
        super(id, name, description, startingPrice, "ELECTRONICS", sellerId);
        this.warrantyMonths = warrantyMonths;
        this.brand = brand;
        this.model = model;
    }

    public Electronics(String id, String name, String description, double startingPrice, String sellerId) {
        this(id, name, description, startingPrice, 12, "Generic Brand", "Generic Model", sellerId);
    }

    public int getWarrantyMonths() {
        return warrantyMonths;
    }

    public void setWarrantyMonths(int warrantyMonths) {
        if (warrantyMonths < 0) {
            throw new IllegalArgumentException("Warranty months cannot be negative");
        }
        this.warrantyMonths = warrantyMonths;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public void printInfo() {
        System.out.println("--- Hàng Điện Tử ---");
        System.out.println("Tên: " + this.name);
        System.out.println("Thương hiệu: " + this.brand);
        System.out.println("Model: " + this.model);
        System.out.println("Mô tả: " + this.description);
        System.out.println("Bảo hành: " + this.warrantyMonths + " tháng");
        System.out.println("Giá khởi điểm: $" + this.startingPrice);
        System.out.println("Giá hiện tại: $" + this.currentHighestPrice);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Electronics that = (Electronics) o;
        return Objects.equals(brand, that.brand) &&
                Objects.equals(model, that.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), brand, model);
    }

    @Override
    public String toString() {
        return "Electronics{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", warrantyMonths=" + warrantyMonths +
                ", startingPrice=" + startingPrice +
                ", currentHighestPrice=" + currentHighestPrice +
                '}';
    }
}