package auction.client;

import auction.network.Message;
import auction.network.MessageType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDateTime;

public class SellerController {

    @FXML private TextField tfItemName;
    @FXML private TextField tfDescription;
    @FXML private TextField tfStartingPrice;
    @FXML private ComboBox<String> cbItemType;
    @FXML private TextField tfDurationMinutes;
    @FXML private Label statusLabel;
    @FXML private Label labelWelcome;

    @FXML
    public void initialize() {
        ClientNetwork net = ClientApp.getNetwork();
        labelWelcome.setText("👋 Xin chào, " + net.getCurrentUsername() + " | Kênh Người Bán");
        
        cbItemType.getItems().addAll("ELECTRONICS", "ART", "VEHICLE");
        cbItemType.setValue("ELECTRONICS");
        
        statusLabel.setText("");
    }

    @FXML
    private void handleCreateProduct() {
        String name = tfItemName.getText().trim();
        String desc = tfDescription.getText().trim();
        String priceStr = tfStartingPrice.getText().trim();
        String type = cbItemType.getValue();
        String durationStr = tfDurationMinutes.getText().trim();

        if (name.isEmpty() || desc.isEmpty() || priceStr.isEmpty() || durationStr.isEmpty()) {
            statusLabel.setText("⚠️ Vui lòng nhập đầy đủ thông tin sản phẩm!");
            statusLabel.setStyle("-fx-text-fill: #c62828; -fx-font-weight: bold;");
            return;
        }

        try {
            double startingPrice = Double.parseDouble(priceStr);
            if (startingPrice <= 0) {
                statusLabel.setText("⚠️ Giá khởi điểm phải lớn hơn 0!");
                statusLabel.setStyle("-fx-text-fill: #c62828; -fx-font-weight: bold;");
                return;
            }

            int duration = Integer.parseInt(durationStr);
            if (duration <= 0) {
                statusLabel.setText("⚠️ Thời gian đấu giá phải lớn hơn 0 phút!");
                statusLabel.setStyle("-fx-text-fill: #c62828; -fx-font-weight: bold;");
                return;
            }

            // Calculate start and end times in ISO-8601 format (yyyy-MM-ddTHH:mm:ss)
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime endTime = now.plusMinutes(duration);

            Message req = new Message(MessageType.CREATE_ITEM_REQUEST);
            req.put("name", name);
            req.put("description", desc);
            req.put("startingPrice", String.valueOf(startingPrice));
            req.put("itemType", type);
            req.put("startTime", now.toString());
            req.put("endTime", endTime.toString());

            // Clear status and send
            statusLabel.setText("⏳ Đang gửi yêu cầu tạo sản phẩm...");
            statusLabel.setStyle("-fx-text-fill: #0d47a1; -fx-font-weight: bold;");
            
            ClientApp.getNetwork().setOnMessageReceived(this::onMessageReceived);
            ClientApp.getNetwork().sendMessage(req);

        } catch (NumberFormatException e) {
            statusLabel.setText("⚠️ Giá hoặc thời gian nhập vào không hợp lệ!");
            statusLabel.setStyle("-fx-text-fill: #c62828; -fx-font-weight: bold;");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            ClientApp.getNetwork().disconnect();
            ClientApp.switchToLogin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onMessageReceived(Message msg) {
        if (msg.getType() == MessageType.CREATE_ITEM_RESPONSE) {
            String status = msg.get("status");
            Platform.runLater(() -> {
                if ("SUCCESS".equals(status)) {
                    statusLabel.setText("🎉 Đăng bán sản phẩm thành công! Phiên đấu giá đã được kích hoạt.");
                    statusLabel.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
                    
                    // Clear inputs
                    tfItemName.clear();
                    tfDescription.clear();
                    tfStartingPrice.clear();
                    tfDurationMinutes.setText("10");
                } else {
                    statusLabel.setText("❌ Thất bại: " + msg.get("reason"));
                    statusLabel.setStyle("-fx-text-fill: #c62828; -fx-font-weight: bold;");
                }
            });
        }
    }
}
