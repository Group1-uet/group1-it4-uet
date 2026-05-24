package com.Auction.Client.Controller;

import com.Auction.Common.Models.Auction.Auction;
import com.Auction.Common.Service.AuctionService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.util.List;

/**
 * Controller cho BidderDashboard.fxml
 * - Tải danh sách phiên đấu giá và tạo card hiển thị trong FlowPane
 */
public class BidderController {
    @FXML private FlowPane auctionFlowPane;
    @FXML private Label balanceLabel;
    @FXML private TableView myBidsTable;

    private final AuctionService auctionService = AuctionService.getInstance();

    @FXML
    public void initialize() {
        onRefresh(null);
    }

    @FXML
    public void onRefresh(ActionEvent actionEvent) {
        try {
            List<Auction> auctions = auctionService.getActiveAuctions();
            if (auctionFlowPane != null) {
                auctionFlowPane.getChildren().clear();
                for (Auction a : auctions) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/Auction/Client/AuctionCard.fxml"));
                    Node node = loader.load();
                    AuctionCardController ctrl = loader.getController();
                    ctrl.setAuction(a);
                    auctionFlowPane.getChildren().add(node);
                }
            }
            // load my bids (placeholder)
            myBidsTable.getItems().clear();
            myBidsTable.getItems().addAll(auctionService.getUserBidHistory("current-user-id"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onHome(ActionEvent actionEvent) {
        try {
            com.Auction.Client.ClientApp.setRoot("MainDashboard");
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onOpenDeposit(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/Auction/Client/DepositDialog.fxml"));
            javafx.scene.Parent root = loader.load();
            DepositDialogController ctrl = loader.getController();
            ctrl.setTransactionType(DepositDialogController.TransactionType.DEPOSIT);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setTitle("Nạp tiền");
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}