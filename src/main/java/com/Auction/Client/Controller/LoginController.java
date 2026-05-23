package com.Auction.Client.Controller;

import com.Auction.Client.ClientService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {

    // 1. Khai báo các ô nhập liệu từ giao diện
    // Lưu ý: Trong file .fxml, bạn nhớ đặt fx:id="txtUsername" và fx:id="txtPassword" cho 2 ô này nhé
    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    // 2. Xử lý khi bấm nút Đăng nhập
    @FXML
    public void handleLogin(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        // Kiểm tra xem người dùng có để trống ô nhập không
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập đầy đủ tài khoản và mật khẩu!");
            return;
        }

        // Gọi Server để kiểm tra đăng nhập thông qua ClientService
        String result = ClientService.getInstance().sendLoginRequest(username, password);

        // Xử lý kết quả trả về
        if ("LOGIN_SUCCESS".equals(result)) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đăng nhập thành công! Đang vào hệ thống...");
            // TODO: Viết code chuyển sang màn hình chính (Main/Home) tại đây
        } else {
            showAlert(Alert.AlertType.ERROR, "Thất bại", "Sai tài khoản hoặc mật khẩu. Vui lòng thử lại!");
        }
    }

    // 3. Hàm chuyển sang màn hình Đăng ký (Giữ nguyên code cũ của bạn bạn)
    @FXML
    private void handleRegisterNavigation(ActionEvent event) {
        try {
            Parent registerRoot = FXMLLoader.load(getClass().getResource("/com/Auction/Client/Controller/RegisterController.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.getScene().setRoot(registerRoot);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Không thể tìm thấy file RegisterController.fxml");
        }
    }

    // 4. Hàm tiện ích giúp hiển thị các bảng thông báo pop-up đẹp mắt trên màn hình
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}