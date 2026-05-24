package com.Auction.Client.Controller;

import com.Auction.Common.Models.Auction.Auction;
import com.Auction.Common.Models.Auction.BidTransaction;
import com.Auction.Common.Service.AuctionService;
import com.Auction.Common.Exceptions.InvalidBidException;
import com.Auction.Common.Exceptions.AuctionClosedException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

public class AuctionDetailController {

    @FXML private Label titleLabel;
    @FXML private Label statusLabel;
    @FXML private Label categoryLabel;
    @FXML private TextArea descriptionArea;
    @FXML private Label currentPriceLabel;
    @FXML private Label timeRemainingLabel;
    @FXML private TextField bidAmountField;
    @FXML private TableView<BidTransaction> bidsTable;
    @FXML private LineChart<Number, Number> priceChart;

    private final AuctionService auctionService = AuctionService.getInstance();
    private Auction auction;
    private String auctionId;
    private final ObservableList<BidTransaction> bids = FXCollections.observableArrayList();
    private final XYChart.Series<Number, Number> priceSeries = new XYChart.Series<>();
    private final Timer timer = new Timer(true);

    public void setAuctionId(String auctionId) {
        this.auctionId = auctionId;
        loadAuction();
    }

    private void loadAuction() {
        this.auction = auctionService.getAuctionById(auctionId);
        if (auction == null) {
            new Alert(Alert.AlertType.ERROR, "Phiên đấu giá không tồn tại").showAndWait();
            closeWindow();
            return;
        }
        titleLabel.setText(auction.getItem().getName());
        categoryLabel.setText(auction.getItem().getCategory());
        descriptionArea.setText(auction.getItem().getDescription());
        updateUI();

        // fill bids table and chart
        bids.setAll(auction.getBids());
        bidsTable.setItems(bids);

        priceSeries.setName("Giá theo thời gian");
        priceChart.getData().clear();
        priceChart.getData().add(priceSeries);
        for (BidTransaction b : auction.getBids()) {
            long x = b.getTime().atZone(ZoneId.systemDefault()).toEpochSecond();
            priceSeries.getData().add(new XYChart.Data<>(x, b.getAmount()));
        }

        // schedule periodic UI updates (remaining time / status)
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> updateUI());
            }
        }, 0, 1000);
    }

    private void updateUI() {
        if (auction == null) return;
        double price = auction.getLeadingBid() != null ? auction.getLeadingBid().getAmount() : auction.getStartingPrice();
        currentPriceLabel.setText(String.format("%.2f", price));
        statusLabel.setText(auction.getState().name());

        Instant now = Instant.now();
        Instant end = auction.getEndTime();
        if (now.isBefore(end)) {
            Duration d = Duration.between(now, end);
            long hours = d.toHours();
            long minutes = d.toMinutesPart();
            long seconds = d.toSecondsPart();
            timeRemainingLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
        } else {
            timeRemainingLabel.setText("00:00:00");
        }
    }

    @FXML
    public void onBack(ActionEvent actionEvent) {
        closeWindow();
    }

    @FXML
    public void onPlaceBid(ActionEvent actionEvent) {
        if (auction == null) return;
        String text = bidAmountField.getText();
        double amount;
        try {
            amount = Double.parseDouble(text);
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.WARNING, "Giá thầu không hợp lệ").showAndWait();
            return;
        }

        try {
            BidTransaction tx = auctionService.placeBid(auction.getId(), /* bidderId */ "current-user-id", /* bidderName */ "You", amount);
            // update UI: add bid and update chart
            bids.add(tx);
            long x = tx.getTime().atZone(ZoneId.systemDefault()).toEpochSecond();
            priceSeries.getData().add(new XYChart.Data<>(x, tx.getAmount()));
            updateUI();
            bidAmountField.clear();
        } catch (InvalidBidException | AuctionClosedException ex) {
            new Alert(Alert.AlertType.WARNING, ex.getMessage()).showAndWait();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Lỗi khi gửi giá: " + ex.getMessage()).showAndWait();
        }
    }

    private void closeWindow() {
        Stage st = (Stage) titleLabel.getScene().getWindow();
        st.close();
        timer.cancel();
    }
}