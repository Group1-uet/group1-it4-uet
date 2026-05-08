package com.Auction.Client.Controller;

import com.Auction.Client.ClientApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;

import java.net.URL;
import java.util.ResourceBundle;

public class SellerController implements Initializable {

    @FXML
    private ChoiceBox<String> categoryChoice;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Thêm các lựa chọn vào ChoiceBox khi màn hình được tải
        if (categoryChoice != null) {
            categoryChoice.getItems().addAll("Art", "Electronics", "Vehicle");
            // Chọn mặc định lựa chọn đầu tiên nếu muốn
            categoryChoice.setValue("Art");
        }
    }

    public void onRefresh(ActionEvent actionEvent) {
    }

    public void onCreate(ActionEvent actionEvent) {

    }

    public void onBack(ActionEvent actionEvent) {
    }

    public void onDelete(ActionEvent actionEvent) {

    }

    @FXML
    public void onLogout(ActionEvent event) {
        try {
            MainController.currentRole = "SELLER";
            ClientApp.setRoot("Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
