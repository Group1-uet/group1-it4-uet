package com.Auction.Client.Controller;

import com.Auction.Common.Service.UserService;
import com.Auction.Common.Exceptions.AuthenticationException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller cho Login.fxml
 * - Gọi UserService.login(...)
 * - Thiết lập vai trò hiện tại (MainController.currentRole)
 * - Chuyển hướng tới MainDashboard khi đăng nhập thành công
 */
public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private final UserService userService = UserService.getInstance();

    @FXML
    public void onLogin() {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        try {
            // UserService.login trả về đối tượng User (model). Ta chỉ cần role để điều hướng.
            Object user = userService.login(username, password); // kiểu Object để tránh phụ thuộc package model
            String role = extractRole(user);
            if (role != null) {
                MainController.currentRole = role.toUpperCase();
            } else {
                MainController.currentRole = "BIDDER";
            }

            // Sau khi login, chuyển đến MainDashboard (MainController sẽ hiện menu phù hợp theo role)
            com.Auction.Client.ClientApp.setRoot("MainDashboard");
        } catch (AuthenticationException ex) {
            showAlert("Sai tài khoản hoặc mật khẩu!");
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Lỗi khi đăng nhập: " + ex.getMessage());
        }
    }

    @FXML
    public void goToRegister() {
        try {
            com.Auction.Client.ClientApp.setRoot("Register");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Không thể mở màn hình đăng ký.");
        }
    }

    private void showAlert(String content) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }

    // Cố gắng lấy role bằng reflection (tránh lỗi package)
    private String extractRole(Object user) {
        if (user == null) return null;
        try {
            // Thử getRole()
            java.lang.reflect.Method m = user.getClass().getMethod("getRole");
            Object r = m.invoke(user);
            return r == null ? null : r.toString();
        } catch (Exception ignored) {
        }
        try {
            // Thử getUserRole() hoặc getRoleName()
            java.lang.reflect.Method m2 = user.getClass().getMethod("getUserRole");
            Object r2 = m2.invoke(user);
            return r2 == null ? null : r2.toString();
        } catch (Exception ignored) {
        }
        return null;
    }
}