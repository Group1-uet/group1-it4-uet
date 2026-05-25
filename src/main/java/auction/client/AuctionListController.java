package auction.client;

import auction.dao.AuctionDAO;
import auction.model.Auction;
import auction.network.Message;
import auction.network.MessageType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class AuctionListController {

    @FXML private TableView<Auction> auctionTable;
    @FXML private TableColumn<Auction, String> colId;
    @FXML private TableColumn<Auction, String> colItemId;
    @FXML private TableColumn<Auction, Double> colCurrentBid;
    @FXML private TableColumn<Auction, String> colStatus;
    @FXML private TableColumn<Auction, String> colEndTime;
    @FXML private Label statusLabel;

    private ObservableList<Auction> auctionData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("auctionId"));
        colItemId.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        colCurrentBid.setCellValueFactory(new PropertyValueFactory<>("currentHighestBid"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colEndTime.setCellValueFactory(new PropertyValueFactory<>("endTime"));

        auctionTable.setItems(auctionData);

        // Lắng nghe update realtime từ server
        ClientApp.getNetwork().setOnMessageReceived(this::onMessageReceived);

        // Tải danh sách phiên đấu giá từ DB
        loadAuctions();
    }

    private void loadAuctions() {
        try {
            AuctionDAO dao = new AuctionDAO();
            List<Auction> auctions = dao.getAllAuctions();
            auctionData.setAll(auctions);
            statusLabel.setText("Đã tải " + auctions.size() + " phiên đấu giá.");
        } catch (Exception e) {
            statusLabel.setText("❌ Lỗi tải dữ liệu: " + e.getMessage());
        }
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
        if (!"ACTIVE".equals(selected.getStatus())) {
            statusLabel.setText("⚠️ Phiên đấu giá này chưa/đã kết thúc.");
            return;
        }
        try {
            ClientApp.switchToBidding(selected.getAuctionId(), selected.getItemId(), selected.getCurrentHighestBid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onMessageReceived(Message msg) {
        // Khi có bid mới, cập nhật lại danh sách
        if (msg.getType() == MessageType.NEW_BID || msg.getType() == MessageType.AUCTION_CLOSED) {
            loadAuctions();
        }
    }
}
