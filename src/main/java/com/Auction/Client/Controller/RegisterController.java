package com.Auction.Client.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.stage.Stage;
import java.io.IOException;

public class RegisterController {

    @FXML
    private void handleLoginNavigation(ActionEvent event) {
        try {
            // Tải lại file LoginController.fxml
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/com/Auction/Client/Controller/LoginController.fxml"));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.getScene().setRoot(loginRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}