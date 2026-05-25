package auction.model;

import java.util.Objects;

public class Bidder extends User {
    private double accountBalance;
    private int successfulBids;
    private int failedBids;

    public Bidder(String id, String username, String password, String email, double initialBalance) {
        super(id, username, password, email, "BIDDER");
        this.accountBalance = initialBalance;
        this.successfulBids = 0;
        this.failedBids = 0;
    }

    public Bidder(String id, String username, String password, String email) {
        this(id, username, password, email, 10000000.0);
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(double accountBalance) {
        if (accountBalance < 0) {
            throw new IllegalArgumentException("Account balance cannot be negative");
        }
        this.accountBalance = accountBalance;
    }

    public int getSuccessfulBids() {
        return successfulBids;
    }

    public void setSuccessfulBids(int successfulBids) {
        this.successfulBids = successfulBids;
    }

    public int getFailedBids() {
        return failedBids;
    }

    public void setFailedBids(int failedBids) {
        this.failedBids = failedBids;
    }

    public boolean hasEnoughBalance(double bidAmount) {
        return accountBalance >= bidAmount;
    }

    public void deductBalance(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        if (!hasEnoughBalance(amount)) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        this.accountBalance -= amount;
    }

    public void addBalance(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.accountBalance += amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Bidder bidder = (Bidder) o;
        return Double.compare(bidder.accountBalance, accountBalance) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), accountBalance);
    }

    @Override
    public String toString() {
        return "Bidder{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", accountBalance=" + accountBalance +
                ", successfulBids=" + successfulBids +
                ", failedBids=" + failedBids +
                '}';
    }
}