package com.Auction.Client.Controller;

import com.Auction.Common.Models.Auction.Auction;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class AuctionCardController {
    @FXML private Label nameLabel;
    @FXML private Label categoryLabel;
    @FXML private Label priceLabel;
    @FXML private Label endsLabel;
    @FXML private Button openButton;

    private Auction auction;

    public void setAuction(Auction auction) {
        this.auction = auction;
        if (auction == null) return;
        try {
            nameLabel.setText(auction.getItem().getName());
        } catch (Exception e) { nameLabel.setText("N/A"); }
        try {
            categoryLabel.setText(auction.getItem().getCategory());
        } catch (Exception e) { categoryLabel.setText(""); }
        double price = auction.getLeadingBid() != null ? auction.getLeadingBid().getAmount() : auction.getStartingPrice();
        priceLabel.setText(String.format("%.2f $", price));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            endsLabel.setText("Kết thúc: " + auction.getEndTime().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime().format(dtf));
        } catch (Exception e) {
            endsLabel.setText("Kết thúc: -");
        }
    }

    @FXML
    public void openDetail() {
        if (auction == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/Auction/Client/AuctionDetail.fxml"));
            Scene scene = new Scene(loader.load());
            AuctionDetailController ctrl = loader.getController();
            ctrl.setAuctionId(auction.getId());

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Chi tiết phiên đấu giá");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}