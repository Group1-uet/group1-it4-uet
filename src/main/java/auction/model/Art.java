package auction.model;

import java.util.Objects;

public class Art extends Item {
    private String artist;
    private String artType; // PAINTING, SCULPTURE, PHOTOGRAPHY, etc.
    private int yearCreated;
    private String material;
    private boolean isAuthenticated;

    public Art(String id, String name, String description, double startingPrice,
               String artist, String artType, int yearCreated, String material,
               boolean isAuthenticated, String sellerId) {
        super(id, name, description, startingPrice, "ART", sellerId);
        this.artist = artist;
        this.artType = artType;
        this.yearCreated = yearCreated;
        this.material = material;
        this.isAuthenticated = isAuthenticated;
    }

    public Art(String id, String name, String description, double startingPrice, String sellerId) {
        this(id, name, description, startingPrice, "Unknown Artist", "PAINTING", 2026, "Canvas", true, sellerId);
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getArtType() {
        return artType;
    }

    public void setArtType(String artType) {
        this.artType = artType;
    }

    public int getYearCreated() {
        return yearCreated;
    }

    public void setYearCreated(int yearCreated) {
        this.yearCreated = yearCreated;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }

    @Override
    public void printInfo() {
        System.out.println("--- Tác Phẩm Nghệ Thuật ---");
        System.out.println("Tên: " + this.name);
        System.out.println("Nghệ sĩ: " + this.artist);
        System.out.println("Loại: " + this.artType);
        System.out.println("Năm tạo: " + this.yearCreated);
        System.out.println("Chất liệu: " + this.material);
        System.out.println("Xác thực: " + (this.isAuthenticated ? "Có" : "Không"));
        System.out.println("Mô tả: " + this.description);
        System.out.println("Giá khởi điểm: $" + this.startingPrice);
        System.out.println("Giá hiện tại: $" + this.currentHighestPrice);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Art art = (Art) o;
        return yearCreated == art.yearCreated &&
                Objects.equals(artist, art.artist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), artist, yearCreated);
    }

    @Override
    public String toString() {
        return "Art{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                ", artType='" + artType + '\'' +
                ", yearCreated=" + yearCreated +
                ", material='" + material + '\'' +
                ", isAuthenticated=" + isAuthenticated +
                ", startingPrice=" + startingPrice +
                ", currentHighestPrice=" + currentHighestPrice +
                '}';
    }
}

