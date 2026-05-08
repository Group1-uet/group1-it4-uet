package com.Auction.Client.Controller;

import com.Auction.Client.ClientApp;
import javafx.event.ActionEvent;

public class BidderController {
    public void onRefresh(ActionEvent actionEvent) {
    }

    public void onBack(ActionEvent actionEvent) {
        try {
            MainController.currentRole = "BIDDER";
            // Quay về màn hình chính
            ClientApp.setRoot("MainDashboard");
        } catch (java.io.IOException e) {
            e.printStackTrace();
            System.out.println("Lỗi: Không thể quay lại MainDashboard từ giao diện Bidder");
        }
    }
}
