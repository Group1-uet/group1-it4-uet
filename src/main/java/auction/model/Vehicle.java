package auction.model;

import java.util.Objects;

public class Vehicle extends Item {
    private String vehicleType; // CAR, MOTORCYCLE, TRUCK, etc.
    private String brand;
    private String model;
    private int yearOfManufacture;
    private String fuelType;
    private int mileage;

    public Vehicle(String id, String name, String description, double startingPrice,
                   String vehicleType, String brand, String model, int yearOfManufacture,
                   String fuelType, int mileage, String sellerId) {
        super(id, name, description, startingPrice, "VEHICLE", sellerId);
        this.vehicleType = vehicleType;
        this.brand = brand;
        this.model = model;
        this.yearOfManufacture = yearOfManufacture;
        this.fuelType = fuelType;
        this.mileage = mileage;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
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

    public int getYearOfManufacture() {
        return yearOfManufacture;
    }

    public void setYearOfManufacture(int yearOfManufacture) {
        this.yearOfManufacture = yearOfManufacture;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        if (mileage < 0) {
            throw new IllegalArgumentException("Mileage cannot be negative");
        }
        this.mileage = mileage;
    }

    @Override
    public void printInfo() {
        System.out.println("--- Phương Tiện ---");
        System.out.println("Loại: " + this.vehicleType);
        System.out.println("Tên: " + this.name);
        System.out.println("Thương hiệu: " + this.brand);
        System.out.println("Model: " + this.model);
        System.out.println("Năm sản xuất: " + this.yearOfManufacture);
        System.out.println("Loại nhiên liệu: " + this.fuelType);
        System.out.println("Số km: " + this.mileage);
        System.out.println("Mô tả: " + this.description);
        System.out.println("Giá khởi điểm: $" + this.startingPrice);
        System.out.println("Giá hiện tại: $" + this.currentHighestPrice);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Vehicle vehicle = (Vehicle) o;
        return yearOfManufacture == vehicle.yearOfManufacture &&
                Objects.equals(vehicleType, vehicle.vehicleType) &&
                Objects.equals(brand, vehicle.brand) &&
                Objects.equals(model, vehicle.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), vehicleType, brand, model, yearOfManufacture);
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", yearOfManufacture=" + yearOfManufacture +
                ", fuelType='" + fuelType + '\'' +
                ", mileage=" + mileage +
                ", startingPrice=" + startingPrice +
                ", currentHighestPrice=" + currentHighestPrice +
                '}';
    }
}

