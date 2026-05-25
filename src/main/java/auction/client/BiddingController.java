package auction.client;

import auction.model.BidTransaction;
import auction.network.Message;
import auction.network.MessageType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BiddingController {

    @FXML private Label labelAuctionTitle;
    @FXML private Label labelCurrentBid;
    @FXML private Label labelStatus;
    @FXML private TextField bidAmountField;
    @FXML private Button placeBidButton;
    @FXML private TableView<BidTransaction> bidHistoryTable;
    @FXML private TableColumn<BidTransaction, String> colBidder;
    @FXML private TableColumn<BidTransaction, Double> colAmount;
    @FXML private TableColumn<BidTransaction, String> colTime;
    @FXML private LineChart<Number, Number> priceChart;
    @FXML private NumberAxis xAxis;
    @FXML private NumberAxis yAxis;

    private String auctionId;
    private double currentPrice;
    private long startTimestamp;
    private XYChart.Series<Number, Number> priceSeries;
    private ObservableList<BidTransaction> bidHistory = FXCollections.observableArrayList();

    public void initData(String auctionId, String auctionTitle, double currentPrice) {
        this.auctionId = auctionId;
        this.currentPrice = currentPrice;
        this.startTimestamp = System.currentTimeMillis();

        labelAuctionTitle.setText("📦 " + auctionTitle);
        labelCurrentBid.setText(String.format("%,.0f ₫", currentPrice));
        labelStatus.setText("🟢 Đang diễn ra");

        // Setup biểu đồ giá realtime
        priceSeries = new XYChart.Series<>();
        priceSeries.setName("Giá đấu (₫)");
        priceChart.getData().add(priceSeries);
        addPricePoint(currentPrice); // Điểm khởi đầu

        // Lắng nghe update từ Server
        ClientApp.getNetwork().setOnMessageReceived(this::onMessageReceived);
    }

    @FXML
    public void initialize() {
        colBidder.setCellValueFactory(new PropertyValueFactory<>("bidderId"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("bidAmount"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        bidHistoryTable.setItems(bidHistory);

        xAxis.setLabel("Thời gian (giây)");
        yAxis.setLabel("Giá (₫)");
        priceChart.setAnimated(false);
    }

    @FXML
    private void handlePlaceBid() {
        String amountText = bidAmountField.getText().trim();
        if (amountText.isEmpty()) {
            labelStatus.setText("⚠️ Vui lòng nhập số tiền đặt giá!");
            return;
        }
        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= currentPrice) {
                labelStatus.setText("⚠️ Giá đặt phải lớn hơn giá hiện tại: " + String.format("%,.0f ₫", currentPrice));
                return;
            }
            Message bidMsg = new Message(MessageType.BID_REQUEST);
            bidMsg.put("auctionId", auctionId);
            bidMsg.put("amount", String.valueOf(amount));
            ClientApp.getNetwork().sendMessage(bidMsg);

            placeBidButton.setDisable(true);
            labelStatus.setText("⏳ Đang gửi lệnh đấu giá...");
        } catch (NumberFormatException e) {
            labelStatus.setText("⚠️ Số tiền không hợp lệ!");
        }
    }

    @FXML
    private void handleBack() throws Exception {
        ClientApp.switchToAuctionList();
    }

    private void onMessageReceived(Message msg) {
        switch (msg.getType()) {
            case NEW_BID:
                if (auctionId.equals(msg.get("auctionId"))) {
                    double newAmount = Double.parseDouble(msg.get("amount"));
                    String bidderId = msg.get("bidderId");

                    // Cập nhật giá hiển thị
                    currentPrice = newAmount;
                    labelCurrentBid.setText(String.format("%,.0f ₫", newAmount));
                    labelStatus.setText("🔥 Bid mới từ: " + bidderId);

                    // Thêm vào lịch sử
                    BidTransaction bt = new BidTransaction(
                            String.valueOf(System.currentTimeMillis()),
                            auctionId, bidderId, newAmount);
                    bidHistory.add(0, bt);

                    // Cập nhật biểu đồ realtime
                    addPricePoint(newAmount);

                    placeBidButton.setDisable(false);
                    bidAmountField.clear();
                }
                break;
            case BID_RESPONSE:
                String status = msg.get("status");
                if ("FAILED".equals(status)) {
                    labelStatus.setText("❌ Đặt giá thất bại: " + msg.get("reason"));
                    placeBidButton.setDisable(false);
                }
                break;
            case AUCTION_EXTENDED:
                if (auctionId.equals(msg.get("auctionId"))) {
                    labelStatus.setText("⏱ Phiên được gia hạn đến: " + msg.get("newEndTime"));
                }
                break;
            case AUCTION_CLOSED:
                if (auctionId.equals(msg.get("auctionId"))) {
                    String winner = msg.get("winner");
                    labelStatus.setText("🏁 Phiên kết thúc! Người thắng: " + winner);
                    placeBidButton.setDisable(true);
                }
                break;
            default:
                break;
        }
    }

    private void addPricePoint(double price) {
        long elapsedSeconds = (System.currentTimeMillis() - startTimestamp) / 1000;
        priceSeries.getData().add(new XYChart.Data<>(elapsedSeconds, price));
    }
}
