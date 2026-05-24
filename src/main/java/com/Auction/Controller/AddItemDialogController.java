package com.Auction.Controller;

import com.Auction.Common.Models.Item.Item;
import com.Auction.Common.Models.Item.ItemFactory;
import com.Auction.Common.Service.AuctionService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller cho AddItemDialog.fxml
 * - Tạo phiên đấu giá mới với ItemFactory
 */
public class AddItemDialogController {
    @FXML private ComboBox<String> categoryChoice;
    @FXML private TextField nameField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField startPriceField;
    @FXML private TextField durationField;
    @FXML private TextField brandField;
    @FXML private TextField artistField;
    @FXML private TextField engineTypeField;
    @FXML private TextField stateField;
    @FXML private Label messageLabel;
    @FXML private javafx.scene.control.Button submitButton;
    @FXML private javafx.scene.control.Button cancelButton;

    private final AuctionService auctionService = AuctionService.getInstance();
    private String currentSellerId = "current-user-id"; // Placeholder

    @FXML
    public void initialize() {
        if (categoryChoice != null) {
            categoryChoice.getItems().addAll(ItemFactory.supportedCategories());
            categoryChoice.setValue(ItemFactory.supportedCategories()[0]);
        }
    }

    @FXML
    public void onSubmit() {
        try {
            // Kiểm tra dữ liệu
            String category = categoryChoice.getValue();
            String name = nameField.getText().trim();
            String description = descriptionArea.getText().trim();
            String startPriceText = startPriceField.getText().trim();
            String durationText = durationField.getText().trim();

            if (name.isEmpty()) {
                showError("Tên sản phẩm không được để trống!");
                return;
            }
            if (description.isEmpty()) {
                showError("Mô tả không được để trống!");
                return;
            }

            double startPrice = Double.parseDouble(startPriceText);
            int durationMinutes = Integer.parseInt(durationText);

            if (startPrice <= 0) {
                showError("Giá khởi điểm phải lớn hơn 0!");
                return;
            }
            if (durationMinutes <= 0) {
                showError("Thời lượng phải lớn hơn 0!");
                return;
            }

            // Chuẩn bị thông tin chi tiết theo loại hàng
            Map<String, String> props = new HashMap<>();
            if (!brandField.getText().isEmpty()) props.put("brand", brandField.getText());
            if (!artistField.getText().isEmpty()) props.put("artist", artistField.getText());
            if (!engineTypeField.getText().isEmpty()) props.put("engineType", engineTypeField.getText());
            if (!stateField.getText().isEmpty()) props.put("state", stateField.getText());

            // Tạo Item thông qua ItemFactory
            Item item = ItemFactory.create(category, name, description, currentSellerId, props);

            // Tính thời gian kết thúc
            Instant startTime = Instant.now();
            Instant endTime = startTime.plus(Duration.ofMinutes(durationMinutes));

            // Tạo phiên đấu giá
            auctionService.createAuction(item, startPrice, startTime, endTime);

            showSuccess("Tạo phiên đấu giá thành công!");
            closeDialogAfterDelay();

        } catch (NumberFormatException nfe) {
            showError("Giá hoặc thời lượng không hợp lệ!");
        } catch (IllegalArgumentException iae) {
            showError("Lỗi: " + iae.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Lỗi: " + ex.getMessage());
        }
    }

    @FXML
    public void onCancel() {
        closeDialog();
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: #ef4444;");
    }

    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: #10b981;");
    }

    private void closeDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void closeDialogAfterDelay() {
        javafx.application.Platform.runLater(() -> {
            try {
                Thread.sleep(1500);
                closeDialog();
            } catch (InterruptedException e) {
                closeDialog();
            }
        });
    }
}