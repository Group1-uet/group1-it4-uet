package com.Auction.Client.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {


    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button registerButton;

    @FXML
    private void onRegister() {
        String user = usernameField.getText();
        String email = emailField.getText();
        String pass = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (user.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
        } else if (!pass.equals(confirm)) {
            errorLabel.setText("Passwords do not match!");
        } else {
            errorLabel.setText(""); // Xóa thông báo lỗi nếu ổn
            System.out.println("Đang gửi yêu cầu đăng ký cho: " + user);
            // Gọi API đăng ký tại đây
        }
    }

    @FXML
    private void onGoToLogin() {
        // Thêm logic chuyển scene về Login tại đây
        System.out.println("Chuyển về màn hình đăng nhập");
    }


}