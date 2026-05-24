package com.Auction.Client.Controller;

import com.Auction.Common.Service.AuctionService;
import com.Auction.Common.Service.UserService;
import com.Auction.Common.Models.Auction.Auction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.util.List;

/**
 * Controller cho AdminDashboard.fxml
 * - Hiển thị danh sách người dùng và danh sách phiên đấu giá
 * - Cho phép refresh và hủy phiên
 */
public class AdminController {
    @FXML private TableView usersTable;
    @FXML private TableColumn userNameCol;
    @FXML private TableColumn userDisplayCol;
    @FXML private TableColumn userRoleCol;

    @FXML private TableView<Auction> auctionsTable;
    @FXML private TableColumn<Auction, String> auctionNameCol;
    @FXML private TableColumn<Auction, String> auctionOwnerCol;
    @FXML private TableColumn<Auction, String> auctionStateCol;
    @FXML private TableColumn<Auction, String> auctionPriceCol;

    private final UserService userService = UserService.getInstance();
    private final AuctionService auctionService = AuctionService.getInstance();

    @FXML
    public void initialize() {
        // Cấu hình columns của auctionsTable (typed Auction)
        auctionNameCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItem().getName()));
        auctionOwnerCol.setCellValueFactory(param -> new SimpleStringProperty(
                param.getValue().getItem().getSellerId() == null ? "" : param.getValue().getItem().getSellerId()));
        auctionStateCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getState().name()));
        auctionPriceCol.setCellValueFactory(param -> {
            double p = param.getValue().getLeadingBid() != null ? param.getValue().getLeadingBid().getAmount() : param.getValue().getStartingPrice();
            return new SimpleStringProperty(String.format("%.2f", p));
        });

        onReloadUsers(null);
        onReloadAuctions(null);
    }

    @FXML
    public void onReloadUsers(ActionEvent actionEvent) {
        try {
            List<Object> users = userService.getAllUsers(); // trả về danh sách model user (kiểu không cố định ở controller)
            ObservableList<Object> items = FXCollections.observableArrayList(users);
            usersTable.getItems().clear();
            usersTable.setItems(items);
            // Thiết lập cell factories bằng reflection để lấy username/display/role
            userNameCol.setCellValueFactory(cell -> new SimpleStringProperty(getProp(cell.getValue(), "getUsername")));
            userDisplayCol.setCellValueFactory(cell -> new SimpleStringProperty(getProp(cell.getValue(), "getDisplayName", "getUsername")));
            userRoleCol.setCellValueFactory(cell -> new SimpleStringProperty(getProp(cell.getValue(), "getRole")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onReloadAuctions(ActionEvent actionEvent) {
        try {
            List<Auction> list = auctionService.getAllAuctions();
            ObservableList<Auction> obs = FXCollections.observableArrayList(list);
            auctionsTable.setItems(obs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onCancel(ActionEvent actionEvent) {
        try {
            Auction selected = auctionsTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                new Alert(Alert.AlertType.INFORMATION, "Vui lòng chọn một phiên để hủy.", ButtonType.OK).showAndWait();
                return;
            }
            selected.cancelAuction();
            // refresh
            onReloadAuctions(null);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Không thể hủy phiên: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    public void onLogout() {
        try {
            com.Auction.Client.ClientApp.setRoot("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onMain(ActionEvent actionEvent) {
        try {
            MainController.currentRole = "ADMIN";
            com.Auction.Client.ClientApp.setRoot("MainDashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Reflection helper: try methodName, if not exists try fallbackMethod (or return empty)
    private String getProp(Object o, String methodName, String fallback) {
        String v = getProp(o, methodName);
        if (v == null && fallback != null) return getProp(o, fallback);
        return v == null ? "" : v;
    }
    private String getProp(Object o, String methodName) {
        if (o == null) return "";
        try {
            java.lang.reflect.Method m = o.getClass().getMethod(methodName);
            Object r = m.invoke(o);
            return r == null ? "" : r.toString();
        } catch (Exception ex) {
            return "";
        }
    }
}