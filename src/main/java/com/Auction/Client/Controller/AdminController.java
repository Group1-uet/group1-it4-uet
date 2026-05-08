package com.Auction.Client.Controller;

import com.Auction.Client.ClientApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class AdminController {


    public void onReloadUsers(ActionEvent actionEvent) {
    }

    public void onReloadAuctions(ActionEvent actionEvent) {
    }

    public void onCancel(ActionEvent actionEvent) {

    }

    @FXML
    public void onLogout(ActionEvent event) {
        try {
            ClientApp.setRoot("Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onMain(ActionEvent actionEvent) {
        try {
            MainController.currentRole = "ADMIN";
            ClientApp.setRoot("MainDashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
