package com.Auction.Client.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;

public class LoginController {

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


}