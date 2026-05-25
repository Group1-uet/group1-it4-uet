package auction.client;

import auction.model.Auction;
import auction.model.BidTransaction;
import auction.network.Message;
import auction.network.MessageType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BiddingController {

    @FXML private Label labelAuctionTitle;
    @FXML private Label labelCurrentBid;
    @FXML private Label labelStatus;
    
    // Manual Bid
    @FXML private TextField bidAmountField;
    @FXML private Button placeBidButton;
    
    // Auto Bid
    @FXML private TextField tfMaxBid;
    @FXML private TextField tfIncrement;
    @FXML private Button btnToggleAutoBid;
    @FXML private Label labelAutoBidStatus;

    // History Table
    @FXML private TableView<BidTransaction> bidHistoryTable;
    @FXML private TableColumn<BidTransaction, String> colBidder;
    @FXML private TableColumn<BidTransaction, Double> colAmount;
    @FXML private TableColumn<BidTransaction, LocalDateTime> colTime;

    // Chart
    @FXML private LineChart<Number, Number> priceChart;
    @FXML private NumberAxis xAxis;
    @FXML private NumberAxis yAxis;
    
    // Timer
    @FXML private Label labelTimer;
    private ScheduledExecutorService countdownExecutor;

    private Auction auction;
    private String auctionId;
    private double currentPrice;
    private XYChart.Series<Number, Number> priceSeries;
    private ObservableList<BidTransaction> bidHistory = FXCollections.observableArrayList();
    private final Gson gson = new Gson();
    
    private boolean isAutoBidActive = false;

    public void initData(Auction auction) {
        this.auction = auction;
        this.auctionId = auction.getAuctionId();
        this.currentPrice = auction.getCurrentHighestBid();

        labelAuctionTitle.setText("📦 " + auction.getItemName() + " (Khởi điểm: " + String.format("%,.0f ₫", auction.getStartingPrice()) + ")");
        labelCurrentBid.setText(String.format("%,.0f ₫", currentPrice));
        
        // Show status
        updateStatusLabel(auction.getStatus());

        // Setup price chart
        priceSeries = new XYChart.Series<>();
        priceSeries.setName("Giá đấu (₫)");
        priceChart.getData().clear();
        priceChart.getData().add(priceSeries);

        // Add starting price point at time 0
        priceSeries.getData().add(new XYChart.Data<>(0, auction.getStartingPrice()));

        // Set message listener
        ClientApp.getNetwork().setOnMessageReceived(this::onMessageReceived);

        // Request bid history from Server
        Message reqHistory = new Message(MessageType.GET_BID_HISTORY_REQUEST);
        reqHistory.put("auctionId", auctionId);
        ClientApp.getNetwork().sendMessage(reqHistory);

        // Setup Auto-Bid panel text
        labelAutoBidStatus.setText("Auto-Bid: Tắt");

        // Start countdown timer
        startCountdownTimer();
        
        // Check if manual bid button should be disabled
        if ("FINISHED".equals(auction.getStatus()) || "PAID".equals(auction.getStatus()) 
                || "CANCELED".equals(auction.getStatus()) || "CLOSED".equals(auction.getStatus())) {
            placeBidButton.setDisable(true);
            btnToggleAutoBid.setDisable(true);
        }
    }

    @FXML
    public void initialize() {
        colBidder.setCellValueFactory(new PropertyValueFactory<>("bidderId"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("bidAmount"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        bidHistoryTable.setItems(bidHistory);

        colAmount.setCellFactory(tc -> new TableCell<BidTransaction, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f ₫", amount));
                }
            }
        });

        colTime.setCellFactory(tc -> new TableCell<BidTransaction, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime time, boolean empty) {
                super.updateItem(time, empty);
                if (empty || time == null) {
                    setText(null);
                } else {
                    setText(time.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                }
            }
        });

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
            
            // Check wallet balance
            double balance = ClientApp.getNetwork().getCurrentUserBalance();
            if (balance < amount) {
                labelStatus.setText("⚠️ Số dư tài khoản không đủ! (Số dư: " + String.format("%,.0f ₫", balance) + ")");
                return;
            }

            Message bidMsg = new Message(MessageType.BID_REQUEST);
            bidMsg.put("auctionId", auctionId);
            bidMsg.put("amount", String.valueOf(amount));
            ClientApp.getNetwork().sendMessage(bidMsg);

            placeBidButton.setDisable(true);
            labelStatus.setText("⏳ Đang gửi lệnh đặt giá...");
        } catch (NumberFormatException e) {
            labelStatus.setText("⚠️ Số tiền không hợp lệ!");
        }
    }

    @FXML
    private void handleToggleAutoBid() {
        if (!isAutoBidActive) {
            // Activate Auto-Bid
            String maxBidText = tfMaxBid.getText().trim();
            String incrementText = tfIncrement.getText().trim();

            if (maxBidText.isEmpty() || incrementText.isEmpty()) {
                labelAutoBidStatus.setText("⚠️ Điền đầy đủ Max Bid & Bước giá!");
                return;
            }

            try {
                double maxBid = Double.parseDouble(maxBidText);
                double increment = Double.parseDouble(incrementText);

                if (maxBid <= currentPrice) {
                    labelAutoBidStatus.setText("⚠️ Max Bid phải lớn hơn giá hiện tại!");
                    return;
                }
                if (increment <= 0) {
                    labelAutoBidStatus.setText("⚠️ Bước giá phải lớn hơn 0!");
                    return;
                }

                Message req = new Message(MessageType.AUTO_BID_REQUEST);
                req.put("auctionId", auctionId);
                req.put("maxBid", String.valueOf(maxBid));
                req.put("increment", String.valueOf(increment));
                ClientApp.getNetwork().sendMessage(req);

                labelAutoBidStatus.setText("⏳ Đang gửi yêu cầu...");
            } catch (NumberFormatException e) {
                labelAutoBidStatus.setText("⚠️ Giá trị số không hợp lệ!");
            }
        } else {
            // Cancel Auto-Bid
            Message req = new Message(MessageType.AUTO_BID_CANCEL);
            req.put("auctionId", auctionId);
            ClientApp.getNetwork().sendMessage(req);

            isAutoBidActive = false;
            tfMaxBid.setDisable(false);
            tfIncrement.setDisable(false);
            btnToggleAutoBid.setText("⚡ BẬT AUTO-BID");
            btnToggleAutoBid.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");
            labelAutoBidStatus.setText("Auto-Bid: Tắt");
        }
    }

    @FXML
    private void handleBack() throws Exception {
        // Unregister auto-bid if leaving screen
        if (isAutoBidActive) {
            Message req = new Message(MessageType.AUTO_BID_CANCEL);
            req.put("auctionId", auctionId);
            ClientApp.getNetwork().sendMessage(req);
        }
        // Stop countdown timer
        stopCountdownTimer();
        ClientApp.switchToAuctionList();
    }

    private void startCountdownTimer() {
        countdownExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "BiddingCountdown");
            t.setDaemon(true);
            return t;
        });
        countdownExecutor.scheduleAtFixedRate(() -> {
            if (auction == null) return;
            long remaining = auction.getRemainingSeconds();
            Platform.runLater(() -> {
                if (labelTimer == null) return;
                if (remaining <= 0) {
                    labelTimer.setText("⏱ Hết giờ!");
                    labelTimer.setStyle("-fx-text-fill: #e94560; -fx-font-size: 14px; -fx-font-weight: bold;");
                } else {
                    long mins = remaining / 60;
                    long secs = remaining % 60;
                    String display = String.format("⏱ Còn lại: %02d:%02d", mins, secs);
                    labelTimer.setText(display);
                    if (remaining <= 60) {
                        labelTimer.setStyle("-fx-text-fill: #e94560; -fx-font-size: 14px; -fx-font-weight: bold;");
                    } else if (remaining <= 300) {
                        labelTimer.setStyle("-fx-text-fill: #f5c518; -fx-font-size: 14px; -fx-font-weight: bold;");
                    } else {
                        labelTimer.setStyle("-fx-text-fill: #4ecdc4; -fx-font-size: 14px; -fx-font-weight: bold;");
                    }
                }
            });
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void stopCountdownTimer() {
        if (countdownExecutor != null && !countdownExecutor.isShutdown()) {
            countdownExecutor.shutdownNow();
        }
    }

    private void updateStatusLabel(String status) {
        if ("RUNNING".equals(status)) {
            labelStatus.setText("🟢 Đang diễn ra");
        } else if ("FINISHED".equals(status)) {
            labelStatus.setText("🏁 Phiên kết thúc (Đang chờ thanh toán)");
            placeBidButton.setDisable(true);
            btnToggleAutoBid.setDisable(true);
        } else if ("PAID".equals(status)) {
            labelStatus.setText("✅ Đã thanh toán & hoàn tất!");
            placeBidButton.setDisable(true);
            btnToggleAutoBid.setDisable(true);
        } else if ("CANCELED".equals(status)) {
            labelStatus.setText("❌ Đã bị hủy bỏ");
            placeBidButton.setDisable(true);
            btnToggleAutoBid.setDisable(true);
        } else {
            labelStatus.setText("🟡 Chưa bắt đầu (OPEN)");
        }
    }

    private void onMessageReceived(Message msg) {
        switch (msg.getType()) {
            case GET_BID_HISTORY_RESPONSE:
                String bidsJson = msg.get("bids");
                Type listType = new TypeToken<List<BidTransaction>>(){}.getType();
                List<BidTransaction> bids = gson.fromJson(bidsJson, listType);
                
                Platform.runLater(() -> {
                    bidHistory.setAll(bids);
                    
                    // Clear and plot history sorted chronologically
                    priceSeries.getData().clear();
                    priceSeries.getData().add(new XYChart.Data<>(0, auction.getStartingPrice()));
                    
                    bids.stream()
                        .sorted(Comparator.comparing(BidTransaction::getTimestamp))
                        .forEach(this::plotBidOnChart);
                });
                break;

            case NEW_BID:
                if (auctionId.equals(msg.get("auctionId"))) {
                    double newAmount = Double.parseDouble(msg.get("amount"));
                    String bidderId = msg.get("bidderId");

                    currentPrice = newAmount;
                    
                    Platform.runLater(() -> {
                        labelCurrentBid.setText(String.format("%,.0f ₫", newAmount));
                        
                        String myUsername = ClientApp.getNetwork().getCurrentUsername();
                        if (myUsername.equals(bidderId)) {
                            labelStatus.setText("🔥 Bạn đang dẫn đầu phiên!");
                        } else {
                            labelStatus.setText("👉 Bid mới từ: " + bidderId);
                        }

                        // Add to table
                        BidTransaction bt = new BidTransaction(
                                String.valueOf(System.currentTimeMillis()),
                                auctionId, bidderId, newAmount);
                        // Parse local time
                        bt.setTimestamp(LocalDateTime.now());
                        bidHistory.add(0, bt);

                        // Plot on chart
                        plotBidOnChart(bt);

                        placeBidButton.setDisable(false);
                        bidAmountField.clear();
                    });
                }
                break;

            case BID_RESPONSE:
                if (auctionId.equals(msg.get("auctionId"))) {
                    String status = msg.get("status");
                    Platform.runLater(() -> {
                        if ("SUCCESS".equals(status)) {
                            labelStatus.setText("✅ Đặt giá thành công!");
                            if (msg.get("balance") != null) {
                                double newBalance = Double.parseDouble(msg.get("balance"));
                                ClientApp.getNetwork().setCurrentUserBalance(newBalance);
                            }
                        } else {
                            labelStatus.setText("❌ Đặt giá thất bại: " + msg.get("reason"));
                        }
                        placeBidButton.setDisable(false);
                    });
                }
                break;

            case AUTO_BID_RESPONSE:
                if (auctionId.equals(msg.get("auctionId"))) {
                    String status = msg.get("status");
                    Platform.runLater(() -> {
                        if ("SUCCESS".equals(status)) {
                            isAutoBidActive = true;
                            tfMaxBid.setDisable(true);
                            tfIncrement.setDisable(true);
                            btnToggleAutoBid.setText("❌ HỦY AUTO-BID");
                            btnToggleAutoBid.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold;");
                            labelAutoBidStatus.setText("Auto-Bid: Đang chạy (Max: " + String.format("%,.0f ₫", Double.parseDouble(tfMaxBid.getText())) + ")");
                        } else {
                            labelAutoBidStatus.setText("❌ Thất bại: " + msg.get("reason"));
                        }
                    });
                }
                break;

            case AUCTION_EXTENDED:
                if (auctionId.equals(msg.get("auctionId"))) {
                    String newEndTimeStr = msg.get("newEndTime");
                    // Update local auction endTime so countdown timer is accurate
                    try {
                        LocalDateTime newEndTime = LocalDateTime.parse(newEndTimeStr);
                        auction.setEndTime(newEndTime);
                    } catch (Exception ignored) {}
                    Platform.runLater(() -> {
                        labelStatus.setText("⏱ Phiên được gia hạn! Hết lúc: " + newEndTimeStr);
                        labelStatus.setStyle("-fx-text-fill: #f5c518; -fx-font-size: 12px; -fx-font-weight: bold;");
                    });
                }
                break;

            case AUCTION_CLOSED:
                if (auctionId.equals(msg.get("auctionId"))) {
                    String winner = msg.get("winner");
                    String finalStatus = msg.get("status");
                    Platform.runLater(() -> {
                        updateStatusLabel(finalStatus);
                        if (ClientApp.getNetwork().getCurrentUserId().equals(winner)) {
                            labelStatus.setText("🏆 Bạn đã THẮNG cuộc phiên đấu giá này!");
                        } else if (!"NONE".equals(winner)) {
                            labelStatus.setText("🏁 Kết thúc! Người thắng: " + winner);
                        } else {
                            labelStatus.setText("🏁 Kết thúc! Không có người đặt giá.");
                        }
                        placeBidButton.setDisable(true);
                        btnToggleAutoBid.setDisable(true);
                    });
                }
                break;

            default:
                break;
        }
    }

    private void plotBidOnChart(BidTransaction bid) {
        // Calculate seconds elapsed from start of auction
        long elapsedSeconds = Duration.between(auction.getStartTime(), bid.getTimestamp()).getSeconds();
        if (elapsedSeconds < 0) elapsedSeconds = 0; // fallback
        priceSeries.getData().add(new XYChart.Data<>(elapsedSeconds, bid.getBidAmount()));
    }
}
