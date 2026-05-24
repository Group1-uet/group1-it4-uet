package com.Auction.Client.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller cho DepositDialog.fxml
 * - Xử lý nạp/rút tiền
 */
public class DepositDialogController {
    @FXML private Label titleLabel;
    @FXML private TextField amountField;
    @FXML private Label feeNoteLabel;
    @FXML private Label messageLabel;
    @FXML private javafx.scene.control.Button submitButton;
    @FXML private javafx.scene.control.Button cancelButton;

    private TransactionType transactionType = TransactionType.DEPOSIT;
    private String currentUserId = "current-user-id"; // Placeholder

    public enum TransactionType {
        DEPOSIT, WITHDRAW
    }

    public void setTransactionType(TransactionType type) {
        this.transactionType = type;
        if (type == TransactionType.DEPOSIT) {
            titleLabel.setText("Nạp tiền");
            submitButton.setText("Nạp tiền");
            feeNoteLabel.setVisible(false);
        } else {
            titleLabel.setText("Rút tiền");
            submitButton.setText("Rút tiền");
            feeNoteLabel.setVisible(true);
            feeNoteLabel.setText("* Phí rút tiền: 10%");
        }
    }

    @FXML
    public void onSubmit() {
        String amountText = amountField.getText().trim();
        if (amountText.isEmpty()) {
            messageLabel.setText("Vui lòng nhập số tiền!");
            messageLabel.setStyle("-fx-text-fill: #ef4444;");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                messageLabel.setText("Số tiền phải lớn hơn 0!");
                messageLabel.setStyle("-fx-text-fill: #ef4444;");
                return;
            }

            // Xử lý nạp/rút tiền qua service
            if (transactionType == TransactionType.DEPOSIT) {
                handleDeposit(amount);
            } else {
                handleWithdraw(amount);
            }

            messageLabel.setText("Giao dịch thành công!");
            messageLabel.setStyle("-fx-text-fill: #10b981;");

            // Đóng dialog sau 1 giây
            javafx.application.Platform.runLater(() -> {
                try {
                    Thread.sleep(1000);
                    closeDialog();
                } catch (InterruptedException e) {
                    closeDialog();
                }
            });

        } catch (NumberFormatException ex) {
            messageLabel.setText("Số tiền không hợp lệ!");
            messageLabel.setStyle("-fx-text-fill: #ef4444;");
        } catch (Exception ex) {
            ex.printStackTrace();
            messageLabel.setText("Lỗi: " + ex.getMessage());
            messageLabel.setStyle("-fx-text-fill: #ef4444;");
        }
    }

    @FXML
    public void onCancel() {
        closeDialog();
    }

    private void handleDeposit(double amount) throws Exception {
        // TODO: Gọi service để nạp tiền
        // Ví dụ: UserService.getInstance().deposit(currentUserId, amount);
        System.out.println("[DEPOSIT] User: " + currentUserId + " | Amount: $" + amount);
    }

    private void handleWithdraw(double amount) throws Exception {
        // TODO: Gọi service để rút tiền (với phí 10%)
        // Ví dụ: UserService.getInstance().withdraw(currentUserId, amount);
        double fee = amount * 0.1;
        double netAmount = amount - fee;
        System.out.println("[WITHDRAW] User: " + currentUserId + " | Amount: $" + amount + " | Fee: $" + fee + " | Net: $" + netAmount);
    }

    private void closeDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}