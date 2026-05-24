package com.Auction.Client.Controller;

import com.Auction.Common.Models.Item.Item;
import com.Auction.Common.Models.Item.ItemFactory;
import com.Auction.Common.Service.AuctionService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller cho SellerDashboard.fxml
 * - Tạo phiên đấu giá mới
 * - Hiển thị danh sách sản phẩm đang bán
 */
public class SellerController implements Initializable {
    @FXML private ComboBox<String> categoryChoice;
    @FXML private TextField nameField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField startPriceField;
    @FXML private TextField minIncField;
    @FXML private TextField durationField;
    @FXML private TextField antiSnipeWindowField;
    @FXML private Label messageLabel;
    @FXML private TableView myAuctionsTable;

    private final AuctionService auctionService = AuctionService.getInstance();
    private String currentSellerId = "current-user-id"; // Placeholder

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (categoryChoice != null) {
            categoryChoice.getItems().addAll(ItemFactory.supportedCategories());
            categoryChoice.setValue(ItemFactory.supportedCategories()[0]);
        }
    }

    @FXML
    public void onCreate(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/Auction/Client/AddItemDialog.fxml"));
            javafx.scene.Parent root = loader.load();

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setTitle("Đăng sản phẩm mới");
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();

            // Refresh khi đóng dialog
            stage.setOnHidden(e -> onRefresh(null));
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Lỗi: " + e.getMessage());
        }
    }

    @FXML
    public void onRefresh(ActionEvent actionEvent) {
        try {
            var list = auctionService.getAuctionsBySeller(currentSellerId);
            myAuctionsTable.getItems().clear();
            myAuctionsTable.getItems().addAll(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onHome(ActionEvent actionEvent) {
        try {
            MainController.currentRole = "SELLER";
            com.Auction.Client.ClientApp.setRoot("MainDashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onDelete(ActionEvent actionEvent) {
        try {
            Object selected = myAuctionsTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                new Alert(Alert.AlertType.INFORMATION, "Vui lòng chọn 1 mục để xóa.", ButtonType.OK).showAndWait();
                return;
            }
            try {
                java.lang.reflect.Method m = selected.getClass().getMethod("cancelAuction");
                m.invoke(selected);
                onRefresh(null);
            } catch (NoSuchMethodException ex) {
                new Alert(Alert.AlertType.ERROR, "Không thể xóa mục này.").showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Lỗi khi xóa: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    public void onLogout(ActionEvent event) {
        try {
            MainController.currentRole = "SELLER";
            com.Auction.Client.ClientApp.setRoot("Login");
        } catch (Exception e) {
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

    @FXML
    public void onOpenWithdraw(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/Auction/Client/DepositDialog.fxml"));
            javafx.scene.Parent root = loader.load();
            DepositDialogController ctrl = loader.getController();
            ctrl.setTransactionType(DepositDialogController.TransactionType.WITHDRAW);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setTitle("Rút tiền");
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}