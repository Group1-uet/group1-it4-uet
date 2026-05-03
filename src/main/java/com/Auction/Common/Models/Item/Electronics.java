package com.Auction.Common.Models.Item;

import com.Auction.Common.Models.Item.Item;

public class Electronics extends Item{

    private String brand;
    private int warrantyMonths;

    public Electronics(String name, String description, String sellerId,
                       String brand, int warrantyMonths) {
        super(name, description, sellerId);
        this.brand = brand == null ? "" : brand;
        this.warrantyMonths = warrantyMonths;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand == null ? "" : brand;
    }

    public int getWarrantyMonths() {
        return warrantyMonths;
    }

    public void setWarrantyMonths(int warrantyMonths) {
        this.warrantyMonths = warrantyMonths;
    }

    @Override
    public String getCategory() {
        return "Electronics";
    }


}
