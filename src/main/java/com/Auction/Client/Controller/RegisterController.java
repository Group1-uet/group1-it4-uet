package com.Auction.Client.Controller;

import com.Auction.Client.ClientApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    // CHỖ NÀY QUAN TRỌNG: Bạn thiếu các dòng khai báo này
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private Label errorLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (roleComboBox != null) {
            roleComboBox.getItems().addAll("Admin", "Seller", "Bidder");
        }

        errorLabel.setText("");
    }

    @FXML
    public void goToLogin(ActionEvent event) throws IOException {
        ClientApp.setRoot("Login");
    }

    @FXML
    public void onRegister(ActionEvent event) {
        // Lấy dữ liệu từ các field
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String selectedRole = roleComboBox.getValue();
        errorLabel.setText("");

        // 1. Kiểm tra trống tên đăng nhập
        if (username.trim().isEmpty()) {
            errorLabel.setText("Tên đăng nhập không được để trống!");
            return;
        }

        // 2. Kiểm tra mật khẩu (độ dài và khớp nhau)
        if (password.length() < 3) {
            errorLabel.setText("Mật khẩu phải có ít nhất 3 ký tự!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Mật khẩu xác nhận không khớp!");
            return;
        }

        // 3. Kiểm tra Role
        if (selectedRole == null) {
            errorLabel.setText("Vui lòng chọn một vai trò!");
            return;
        }

        // --- PHẦN LOGIC ĐIỀU HƯỚNG THEO ROLE ---
        try {
            switch (selectedRole) {
                case "Admin":
                    // Chuyển đến file AdminDashboard.fxml
                    ClientApp.setRoot("AdminDashboard");
                    break;
                case "Seller":
                    // Chuyển đến file SellerDashboard.fxml
                    ClientApp.setRoot("SellerDashboard");
                    break;
                case "Bidder":
                    // Chuyển đến file BidderDashboard.fxml
                    ClientApp.setRoot("BidderDashboard");
                    break;
                default:
                    errorLabel.setText("Vai trò không hợp lệ!");
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Không có chức năng " + selectedRole);
        }
    }
}