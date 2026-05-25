package auction.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientApp extends Application {

    private static Stage primaryStage;
    private static ClientNetwork clientNetwork = new ClientNetwork();

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        primaryStage.setTitle("Hệ thống Đấu giá Trực tuyến");
        switchToLogin();
        primaryStage.show();
    }

    public static void switchToLogin() throws Exception {
        Parent root = FXMLLoader.load(ClientApp.class.getResource("/fxml/login.fxml"));
        primaryStage.setScene(new Scene(root, 480, 360));
        primaryStage.setResizable(false);
    }

    public static void switchToAuctionList() throws Exception {
        FXMLLoader loader = new FXMLLoader(ClientApp.class.getResource("/fxml/auctionList.fxml"));
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root, 860, 600));
        primaryStage.setResizable(true);
    }

    public static void switchToBidding(String auctionId, String auctionTitle, double currentPrice) throws Exception {
        FXMLLoader loader = new FXMLLoader(ClientApp.class.getResource("/fxml/bidding.fxml"));
        Parent root = loader.load();
        BiddingController controller = loader.getController();
        controller.initData(auctionId, auctionTitle, currentPrice);
        primaryStage.setScene(new Scene(root, 860, 600));
    }

    public static ClientNetwork getNetwork() {
        return clientNetwork;
    }

    @Override
    public void stop() {
        clientNetwork.disconnect();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
