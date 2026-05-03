package com.Auction.Client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class ClientApp extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        primaryStage.setTitle("Online Auction System");

        // Mặc định khởi động vào màn hình Login
        setRoot("Login");
        primaryStage.show();
    }

    // Phương thức dùng để chuyển đổi màn hình (Scene)
    public static void setRoot(String fxml) throws IOException {
        // Đường dẫn file FXML của bạn (điều chỉnh cho đúng package)
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApp.class.getResource("/com/Auction/Client/" + fxml + ".fxml"));
        Parent root = fxmlLoader.load();

        if (primaryStage.getScene() == null) {
            primaryStage.setScene(new Scene(root));
        } else {
            primaryStage.getScene().setRoot(root);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}