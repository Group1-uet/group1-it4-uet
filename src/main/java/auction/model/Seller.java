package auction.model;

import java.util.Objects;

public class Seller extends User {
    private String storeName;
    private double rating;
    private int totalSold;
    private int accountSuspended;

    public Seller(String id, String username, String password, String email, String storeName) {
        super(id, username, password, email, "SELLER");
        this.storeName = storeName;
        this.rating = 5.0;
        this.totalSold = 0;
        this.accountSuspended = 0;
    }

    public Seller(String id, String username, String password, String email) {
        this(id, username, password, email, username + "'s Store");
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }
        this.rating = rating;
    }

    public int getTotalSold() {
        return totalSold;
    }

    public void setTotalSold(int totalSold) {
        this.totalSold = totalSold;
    }

    public int getAccountSuspended() {
        return accountSuspended;
    }

    public void setAccountSuspended(int accountSuspended) {
        this.accountSuspended = accountSuspended;
    }

    public void incrementTotalSold() {
        this.totalSold++;
    }

    public void suspendAccount() {
        this.accountSuspended = 1;
    }

    public void unsuspendAccount() {
        this.accountSuspended = 0;
    }

    public boolean isAccountSuspended() {
        return accountSuspended == 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Seller seller = (Seller) o;
        return Objects.equals(storeName, seller.storeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), storeName);
    }

    @Override
    public String toString() {
        return "Seller{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", storeName='" + storeName + '\'' +
                ", rating=" + rating +
                ", totalSold=" + totalSold +
                ", accountSuspended=" + accountSuspended +
                '}';
    }
}

