package com.Auction.Client.Controller;

import com.Auction.Common.Service.UserService;
import com.Auction.Common.Models.User.UserRole;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller cho Register.fxml
 * - Đăng ký người dùng thông qua UserService.register(...)
 * - Điều hướng sang dashboard tương ứng theo role
 */
public class RegisterController implements Initializable {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Label errorLabel;

    private final UserService userService = UserService.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (roleComboBox != null) {
            roleComboBox.getItems().addAll("Admin", "Seller", "Bidder");
        }
        if (errorLabel != null) errorLabel.setText("");
    }

    @FXML
    public void onRegister() {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();
        String confirm = confirmPasswordField.getText() == null ? "" : confirmPasswordField.getText();
        String selected = roleComboBox.getValue();

        errorLabel.setText("");

        if (username.isEmpty()) {
            errorLabel.setText("Tên đăng nhập không được để trống!");
            return;
        }
        if (password.length() < 3) {
            errorLabel.setText("Mật khẩu phải có ít nhất 3 ký tự!");
            return;
        }
        if (!password.equals(confirm)) {
            errorLabel.setText("Mật khẩu xác nhận không khớp!");
            return;
        }
        if (selected == null) {
            errorLabel.setText("Vui lòng chọn vai trò!");
            return;
        }

        try {
            // Chuyển tên role UI sang enum UserRole (nếu có)
            UserRole roleEnum;
            switch (selected.toUpperCase()) {
                case "ADMIN": roleEnum = UserRole.ADMIN; break;
                case "SELLER": roleEnum = UserRole.SELLER; break;
                default: roleEnum = UserRole.BIDDER; break;
            }

            // register: displayName = username (simple)
            userService.register(username, password, username, roleEnum);

            // Sau khi đăng ký, set role và chuyển màn hình
            MainController.currentRole = roleEnum.name();
            switch (roleEnum) {
                case ADMIN -> com.Auction.Client.ClientApp.setRoot("AdminDashboard");
                case SELLER -> com.Auction.Client.ClientApp.setRoot("SellerDashboard");
                default -> com.Auction.Client.ClientApp.setRoot("BidderDashboard");
            }
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Không thể chuyển màn hình: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Đăng ký thất bại: " + e.getMessage());
        }
    }

    @FXML
    public void goToLogin() {
        try {
            com.Auction.Client.ClientApp.setRoot("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}