package auction.client;

import auction.model.Auction;
import auction.network.Message;
import auction.network.MessageType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.lang.reflect.Type;
import java.util.List;

public class AuctionListController {

    @FXML private Label labelUserWelcome;
    @FXML private TableView<Auction> auctionTable;
    @FXML private TableColumn<Auction, String> colId;
    @FXML private TableColumn<Auction, String> colItemId; // Maps to itemName
    @FXML private TableColumn<Auction, Double> colCurrentBid;
    @FXML private TableColumn<Auction, String> colStatus;
    @FXML private TableColumn<Auction, String> colEndTime;
    @FXML private Label statusLabel;

    private ObservableList<Auction> auctionData = FXCollections.observableArrayList();
    private final Gson gson = auction.network.GsonHelper.getGson();

    @FXML
    public void initialize() {
        // Display welcome and balance
        ClientNetwork net = ClientApp.getNetwork();
        labelUserWelcome.setText("👋 Xin chào, " + net.getCurrentUsername() + " | 💰 Số dư: " + String.format("%,.0f ₫", net.getCurrentUserBalance()));

        colId.setCellValueFactory(new PropertyValueFactory<>("auctionId"));
        colItemId.setCellValueFactory(new PropertyValueFactory<>("itemName")); // Map to itemName for product names!
        colCurrentBid.setCellValueFactory(new PropertyValueFactory<>("currentHighestBid"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colEndTime.setCellValueFactory(new PropertyValueFactory<>("endTime"));

        auctionTable.setItems(auctionData);

        // Custom cell factory to format currency
        colCurrentBid.setCellFactory(tc -> new TableCell<Auction, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f ₫", price));
                }
            }
        });

        // Set message listener
        ClientApp.getNetwork().setOnMessageReceived(this::onMessageReceived);

        // Fetch auctions list
        loadAuctions();
    }

    private void loadAuctions() {
        Message req = new Message(MessageType.GET_AUCTIONS_REQUEST);
        ClientApp.getNetwork().sendMessage(req);
        statusLabel.setText("Đang tải danh sách phiên đấu giá...");
    }

    @FXML
    private void handleRefresh() {
        loadAuctions();
    }

    @FXML
    private void handleJoinAuction() {
        Auction selected = auctionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("⚠️ Vui lòng chọn một phiên đấu giá.");
            return;
        }
        if (!"RUNNING".equals(selected.getStatus())) {
            statusLabel.setText("⚠️ Phiên đấu giá này chưa diễn ra hoặc đã kết thúc.");
            return;
        }
        try {
            ClientApp.switchToBidding(selected);
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("❌ Lỗi tham gia: " + e.getMessage());
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
        if (msg.getType() == MessageType.GET_AUCTIONS_RESPONSE) {
            String auctionsJson = msg.get("auctions");
            Type listType = new TypeToken<List<Auction>>(){}.getType();
            List<Auction> auctions = gson.fromJson(auctionsJson, listType);
            
            Platform.runLater(() -> {
                auctionData.setAll(auctions);
                statusLabel.setText("Đã tải " + auctions.size() + " phiên đấu giá.");
            });
        } else if (msg.getType() == MessageType.NEW_BID || msg.getType() == MessageType.AUCTION_CLOSED || msg.getType() == MessageType.AUCTION_EXTENDED) {
            // Reload list when updates occur
            loadAuctions();
        }
    }
}
