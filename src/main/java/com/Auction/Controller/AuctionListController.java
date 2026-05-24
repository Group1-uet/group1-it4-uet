package com.Auction.Client.Controller;

import com.Auction.Common.Models.Auction.Auction;
import com.Auction.Common.Service.AuctionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Predicate;

public class AuctionListController {

    @FXML private TextField filterField;
    @FXML private TableView<Auction> table;
    @FXML private TableColumn<Auction, String> nameCol;
    @FXML private TableColumn<Auction, String> categoryCol;
    @FXML private TableColumn<Auction, String> priceCol;
    @FXML private TableColumn<Auction, String> endsCol;
    @FXML private TableColumn<Auction, String> stateCol;

    private final AuctionService auctionService = AuctionService.getInstance();
    private final ObservableList<Auction> masterList = FXCollections.observableArrayList();
    private FilteredList<Auction> filteredList;

    @FXML
    public void initialize() {
        // column cell value factories (simple text conversions)
        nameCol.setCellValueFactory(param -> javafx.beans.property.SimpleStringProperty
                .simpleStringProperty(param.getValue().getItem().getName()));
        categoryCol.setCellValueFactory(param -> javafx.beans.property.SimpleStringProperty
                .simpleStringProperty(param.getValue().getItem().getCategory()));
        priceCol.setCellValueFactory(param -> javafx.beans.property.SimpleStringProperty
                .simpleStringProperty(String.format("%.2f",
                        param.getValue().getLeadingBid() != null ? param.getValue().getLeadingBid().getAmount() : param.getValue().getStartingPrice())));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        endsCol.setCellValueFactory(param -> javafx.beans.property.SimpleStringProperty
                .simpleStringProperty(param.getValue().getEndTime().toString()));
        stateCol.setCellValueFactory(param -> javafx.beans.property.SimpleStringProperty
                .simpleStringProperty(param.getValue().getState().name()));

        filteredList = new FilteredList<>(masterList, p -> true);
        table.setItems(filteredList);

        filterField.textProperty().addListener((obs, oldV, newV) -> {
            final String term = newV == null ? "" : newV.trim().toLowerCase();
            filteredList.setPredicate(createFilterPredicate(term));
        });

        onRefresh(null);
    }

    private Predicate<Auction> createFilterPredicate(String term) {
        if (term.isEmpty()) return a -> true;
        return a -> {
            String name = a.getItem().getName().toLowerCase();
            String cat = a.getItem().getCategory() != null ? a.getItem().getCategory().toLowerCase() : "";
            return name.contains(term) || cat.contains(term);
        };
    }

    @FXML
    public void onRefresh(ActionEvent actionEvent) {
        List<Auction> list = auctionService.getActiveAuctions();
        masterList.setAll(list);
    }

    @FXML
    public void onOpen(ActionEvent actionEvent) {
        Auction selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert a = new Alert(Alert.AlertType.INFORMATION, "Vui lòng chọn một phiên để xem chi tiết.", ButtonType.OK);
            a.initOwner(table.getScene().getWindow());
            a.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/Auction/Client/AuctionDetail.fxml"));
            Scene scene = new Scene(loader.load());
            // pass auction id to detail controller
            AuctionDetailController ctrl = loader.getController();
            ctrl.setAuctionId(selected.getId());

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Chi tiết phiên đấu giá");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Không thể mở chi tiết: " + e.getMessage()).showAndWait();
        }
    }
}