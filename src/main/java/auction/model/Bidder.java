package auction.model;

public class Bidder extends User {
    private double accountBalance; // Số dư tài khoản để kiểm tra khi đặt giá

    public Bidder(String id, String username, String password, double initialBalance) {
        super(id, username, password, "BIDDER");
        this.accountBalance = initialBalance;
    }

    public double getAccountBalance() { return accountBalance; }
    public void setAccountBalance(double accountBalance) { this.accountBalance = accountBalance; }
}