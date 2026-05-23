package auction.controller;

import auction.model.Item;
import java.util.concurrent.ConcurrentHashMap;

public class AuctionManager {
    // Áp dụng Singleton Pattern
    private static AuctionManager instance;
    private ConcurrentHashMap<String, Item> activeAuctions;

    private AuctionManager() {
        activeAuctions = new ConcurrentHashMap<>();
    }

    public static synchronized AuctionManager getInstance() {
        if (instance == null) {
            instance = new AuctionManager();
        }
        return instance;
    }

    // Hàm quản lý khởi tạo phiên mới
    public void startNewAuction(String auctionId, Item item) {
        activeAuctions.put(auctionId, item);
        System.out.println("Đã mở phiên đấu giá cho: " + item.getId());
    }
}