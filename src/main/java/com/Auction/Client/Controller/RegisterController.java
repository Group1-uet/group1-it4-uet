package com.Auction.Client.Controller;

import com.Auction.Client.ClientApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.io.IOException;

public class RegisterController {

    @FXML
    public void goToLogin(ActionEvent event) throws IOException {
        // Gọi Main Controller để đổi về màn hình Login
        ClientApp.setRoot("Login");
    }

    @FXML
    public void onRegister(ActionEvent event) {
        System.out.println("Xử lý đăng ký...");
    }
}