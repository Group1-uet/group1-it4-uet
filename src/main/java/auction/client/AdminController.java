package auction.client;

import auction.model.Auction;
import auction.model.User;
import auction.model.Bidder;
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
import java.time.LocalDateTime;
import java.util.List;

public class AdminController {

    // Tab 1 - Auction Management
    @FXML private TableView<Auction> auctionTable;
    @FXML private TableColumn<Auction, String> colId;
    @FXML private TableColumn<Auction, String> colItemId;
    @FXML private TableColumn<Auction, Double> colCurrentBid;
    @FXML private TableColumn<Auction, String> colStatus;
    @FXML private TableColumn<Auction, String> colEndTime;

    // Create Auction Form
    @FXML private TextField tfItemName;
    @FXML private TextField tfDescription;
    @FXML private TextField tfStartingPrice;
    @FXML private ComboBox<String> cbItemType;
    @FXML private TextField tfDurationMinutes;

    // Tab 2 - User Management (NEW)
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> colUserId;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, Double> colUserBalance;

    // Common
    @FXML private Label labelWelcome;
    @FXML private Label statusLabel;

    private ObservableList<Auction> auctionData = FXCollections.observableArrayList();
    private ObservableList<User> userData = FXCollections.observableArrayList();
    private final Gson gson = auction.network.GsonHelper.getGson();

    @FXML
    public void initialize() {
        ClientNetwork net = ClientApp.getNetwork();
        labelWelcome.setText("👋 Xin chào, " + net.getCurrentUsername() + " | Kênh Quản Trị Hệ Thống (Super-User)");

        // 1. Setup Auction Table columns
        colId.setCellValueFactory(new PropertyValueFactory<>("auctionId"));
        colItemId.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colCurrentBid.setCellValueFactory(new PropertyValueFactory<>("currentHighestBid"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colEndTime.setCellValueFactory(new PropertyValueFactory<>("endTime"));

        auctionTable.setItems(auctionData);

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

        // 2. Setup Create Auction Form
        cbItemType.getItems().addAll("ELECTRONICS", "ART", "VEHICLE");
        cbItemType.setValue("ELECTRONICS");

        // 3. Setup Users Table columns
        colUserId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        colUserBalance.setCellValueFactory(cellData -> {
            User u = cellData.getValue();
            if (u instanceof Bidder) {
                return new javafx.beans.property.SimpleDoubleProperty(((Bidder) u).getAccountBalance()).asObject();
            } else {
                return new javafx.beans.property.SimpleDoubleProperty(0.0).asObject();
            }
        });

        usersTable.setItems(userData);

        colUserBalance.setCellFactory(tc -> new TableCell<User, Double>() {
            @Override
            protected void updateItem(Double balance, boolean empty) {
                super.updateItem(balance, empty);
                if (empty || balance == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f ₫", balance));
                }
            }
        });

        statusLabel.setText("");

        // Register network listener
        ClientApp.getNetwork().setOnMessageReceived(this::onMessageReceived);

        // Load data on startup
        loadAuctions();
        loadUsers();
    }

    private void loadAuctions() {
        Message req = new Message(MessageType.GET_AUCTIONS_REQUEST);
        ClientApp.getNetwork().sendMessage(req);
        statusLabel.setText("⏳ Đang tải danh sách đấu giá...");
        statusLabel.setStyle("-fx-text-fill: #0d47a1; -fx-font-weight: bold;");
    }

    private void loadUsers() {
        Message req = new Message(MessageType.GET_USERS_REQUEST);
        ClientApp.getNetwork().sendMessage(req);
    }

    @FXML
    private void handleRefresh() {
        loadAuctions();
    }

    @FXML
    private void handleRefreshUsers() {
        loadUsers();
    }

    @FXML
    private void handleJoinAuction() {
        Auction selected = auctionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("⚠️ Vui lòng chọn một phiên đấu giá để tham gia!");
            statusLabel.setStyle("-fx-text-fill: #c62828; -fx-font-weight: bold;");
            return;
        }
        if (!"RUNNING".equals(selected.getStatus())) {
            statusLabel.setText("⚠️ Phiên đấu giá này chưa diễn ra hoặc đã kết thúc.");
            statusLabel.setStyle("-fx-text-fill: #c62828; -fx-font-weight: bold;");
            return;
        }
        try {
            ClientApp.switchToBidding(selected);
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("❌ Lỗi tham gia: " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: #c62828; -fx-font-weight: bold;");
        }
    }

    @FXML
    private void handleDeleteAuction() {
        Auction selected = auctionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("⚠️ Vui lòng chọn một phiên đấu giá để hủy!");
            statusLabel.setStyle("-fx-text-fill: #c62828; -fx-font-weight: bold;");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận hủy phiên đấu giá");
        alert.setHeaderText("🛑 Bạn có chắc chắn muốn hủy phiên đấu giá này không?");
        alert.setContentText("Hành động này sẽ xóa phiên đấu giá khỏi CSDL SQLite và không thể khôi phục.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Message req = new Message(MessageType.DELETE_AUCTION_REQUEST);
                req.put("auctionId", selected.getAuctionId());
                ClientApp.getNetwork().sendMessage(req);
                statusLabel.setText("⏳ Đang gửi yêu cầu hủy phiên đấu giá...");
                statusLabel.setStyle("-fx-text-fill: #0d47a1; -fx-font-weight: bold;");
            }
        });
    }

    @FXML
    private void handleDeleteUser() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("⚠️ Vui lòng chọn một tài khoản để xóa!");
            statusLabel.setStyle("-fx-text-fill: #c62828; -fx-font-weight: bold;");
            return;
        }

        if (ClientApp.getNetwork().getCurrentUserId().equals(selected.getId())) {
            statusLabel.setText("⚠️ Bạn không thể tự xóa tài khoản của chính mình!");
            statusLabel.setStyle("-fx-text-fill: #c62828; -fx-font-weight: bold;");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa tài khoản");
        alert.setHeaderText("🛑 Bạn có chắc chắn muốn xóa tài khoản này?");
        alert.setContentText("Hành động này sẽ xóa người dùng khỏi CSDL SQLite và không thể khôi phục.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Message req = new Message(MessageType.DELETE_USER_REQUEST);
                req.put("targetUserId", selected.getId());
                ClientApp.getNetwork().sendMessage(req);
                statusLabel.setText("⏳ Đang gửi yêu cầu xóa tài khoản...");
                statusLabel.setStyle("-fx-text-fill: #0d47a1; -fx-font-weight: bold;");
            }
        });
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

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime endTime = now.plusMinutes(duration);

            Message req = new Message(MessageType.CREATE_ITEM_REQUEST);
            req.put("name", name);
            req.put("description", desc);
            req.put("startingPrice", String.valueOf(startingPrice));
            req.put("itemType", type);
            req.put("startTime", now.toString());
            req.put("endTime", endTime.toString());

            statusLabel.setText("⏳ Đang tạo phiên đấu giá mới...");
            statusLabel.setStyle("-fx-text-fill: #0d47a1; -fx-font-weight: bold;");
            
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
        if (msg.getType() == MessageType.GET_AUCTIONS_RESPONSE) {
            String auctionsJson = msg.get("auctions");
            Type listType = new TypeToken<List<Auction>>(){}.getType();
            List<Auction> auctions = gson.fromJson(auctionsJson, listType);

            Platform.runLater(() -> {
                auctionData.setAll(auctions);
                statusLabel.setText("✅ Đã cập nhật danh sách gồm " + auctions.size() + " phiên đấu giá.");
                statusLabel.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
            });
        } else if (msg.getType() == MessageType.GET_USERS_RESPONSE) {
            String usersJson = msg.get("users");
            Type listType = new TypeToken<List<User>>(){}.getType();
            List<User> users = gson.fromJson(usersJson, listType);

            Platform.runLater(() -> {
                userData.setAll(users);
                statusLabel.setText("✅ Đã cập nhật danh sách người dùng.");
                statusLabel.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
            });
        } else if (msg.getType() == MessageType.CREATE_ITEM_RESPONSE) {
            String status = msg.get("status");
            Platform.runLater(() -> {
                if ("SUCCESS".equals(status)) {
                    statusLabel.setText("🎉 Đăng bán & Tạo phiên đấu giá thành công!");
                    statusLabel.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
                    tfItemName.clear();
                    tfDescription.clear();
                    tfStartingPrice.clear();
                    tfDurationMinutes.setText("10");
                    loadAuctions();
                } else {
                    statusLabel.setText("❌ Đăng bán thất bại: " + msg.get("reason"));
                    statusLabel.setStyle("-fx-text-fill: #c62828; -fx-font-weight: bold;");
                }
            });
        } else if (msg.getType() == MessageType.DELETE_AUCTION_RESPONSE) {
            String status = msg.get("status");
            Platform.runLater(() -> {
                if ("SUCCESS".equals(status)) {
                    statusLabel.setText("🎉 Đã hủy phiên đấu giá thành công!");
                    statusLabel.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
                    loadAuctions();
                } else {
                    statusLabel.setText("❌ Hủy phiên thất bại: " + msg.get("reason"));
                    statusLabel.setStyle("-fx-text-fill: #c62828; -fx-font-weight: bold;");
                }
            });
        } else if (msg.getType() == MessageType.DELETE_USER_RESPONSE) {
            String status = msg.get("status");
            Platform.runLater(() -> {
                if ("SUCCESS".equals(status)) {
                    statusLabel.setText("🎉 Đã xóa tài khoản người dùng thành công!");
                    statusLabel.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
                    loadUsers(); // Refresh users table
                } else {
                    statusLabel.setText("❌ Xóa tài khoản thất bại: " + msg.get("reason"));
                    statusLabel.setStyle("-fx-text-fill: #c62828; -fx-font-weight: bold;");
                }
            });
        } else if (msg.getType() == MessageType.NEW_BID || msg.getType() == MessageType.AUCTION_CLOSED || msg.getType() == MessageType.AUCTION_EXTENDED) {
            loadAuctions();
        }
    }
}
