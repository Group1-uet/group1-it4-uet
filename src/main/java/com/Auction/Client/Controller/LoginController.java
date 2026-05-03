package com.Auction.Client.Controller;

import com.Auction.Client.ClientApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.io.IOException;

public class LoginController {

    @FXML
    public void onLogin(ActionEvent event) {
        System.out.println("Xử lý đăng nhập...");
    }

    @FXML
    public void goToRegister(ActionEvent event) throws IOException {
        ClientApp.setRoot("Register");
    }
}