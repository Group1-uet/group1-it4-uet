package com.Auction.Client.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {


    private void navigateToMain(ActionEvent event) {
        try {
            // Tải file FXML của màn hình Main
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/Auction/Client/View/MainDashboard.fxml"));
            Parent mainRoot = loader.load();

            // Lấy Stage hiện tại từ event
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Tạo Scene mới và thiết lập cho Stage
            Scene scene = new Scene(mainRoot);
            stage.setScene(scene);
            stage.centerOnScreen(); // Đưa cửa sổ ra giữa màn hình
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Không thể chuyển sang màn hình Main. Kiểm tra lại đường dẫn file FXML!");
        }
    }

    public void onLogout(ActionEvent actionEvent) {
    }

    public void gotoSeller(ActionEvent actionEvent) {
        try {
            com.Auction.Client.ClientApp.setRoot("Seller");
        } catch (java.io.IOException e) {
            e.printStackTrace();
            System.out.println("Lỗi: Không thể tìm thấy file SellerDashboard.fxml");
        }
    }

    @FXML
    private ToggleButton btnAuctions;
    @FXML
    private ToggleButton btnSeller;
    @FXML
    private ToggleButton btnAdmin;
    @FXML
    private ToggleButton btnDashboard;

    // Biến lưu trữ vai trò hiện tại (có thể set từ các Controller khác)
    public static String currentRole = "USER";

    public void initialize() {
        // Luôn hiện btnDashboard vì đây là mặc định

        // Reset hiển thị
        btnAuctions.setManaged(false);
        btnAuctions.setVisible(false);

        btnSeller.setManaged(false);
        btnSeller.setVisible(false);

        btnAdmin.setManaged(false);
        btnAdmin.setVisible(false);

        // Hiển thị dựa trên vai trò
        if ("ADMIN".equals(currentRole)) {
            showNode(btnAdmin);
        } else if ("SELLER".equals(currentRole)) {
            showNode(btnSeller);
        } else if ("BIDDER".equals(currentRole)) {
            showNode(btnAuctions);
        }
    }

    private void showNode(Node node) {
        node.setManaged(true);
        node.setVisible(true);
    }
}
