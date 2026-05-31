package auction.server;

import auction.engine.AuctionEngine;
import auction.engine.AutoBidManager;
import auction.engine.NotificationManager;
import auction.dao.UserDAO;
import auction.dao.ItemDAO;
import auction.dao.AuctionDAO;
import auction.dao.BidTransactionDAO;
import auction.model.*;
import auction.network.Message;
import auction.network.MessageType;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String userId;
    private final Gson gson;
    private final UserDAO userDAO;
    private final ItemDAO itemDAO;
    private final AuctionDAO auctionDAO;
    private final BidTransactionDAO bidDAO;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.gson = auction.network.GsonHelper.getGson();
        this.userDAO = new UserDAO();
        this.itemDAO = new ItemDAO();
        this.auctionDAO = new AuctionDAO();
        this.bidDAO = new BidTransactionDAO();
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            NotificationManager.getInstance().registerClient(this);
            System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());
            
            Message welcomeMsg = new Message(MessageType.NOTIFICATION);
            welcomeMsg.put("message", "Welcome to Auction Server!");
            sendMessage(welcomeMsg);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                processMessage(inputLine);
            }
        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    private void processMessage(String jsonString) {
        System.out.println("Received: " + jsonString);
        try {
            Message request = gson.fromJson(jsonString, Message.class);
            if (request == null || request.getType() == null) return;

            switch (request.getType()) {
                case REGISTER_REQUEST:
                    handleRegister(request);
                    break;
                case LOGIN_REQUEST:
                    handleLogin(request);
                    break;
                case FORGOT_PASSWORD_REQUEST:
                    handleForgotPassword(request);
                    break;
                case DEPOSIT_REQUEST:
                    handleDeposit(request);
                    break;
                case GET_AUCTIONS_REQUEST:
                    handleGetAuctions(request);
                    break;
                case GET_BID_HISTORY_REQUEST:
                    handleGetBidHistory(request);
                    break;
                case CREATE_ITEM_REQUEST:
                    handleCreateItem(request);
                    break;
                case BID_REQUEST:
                    handlePlaceBid(request);
                    break;
                case AUTO_BID_REQUEST:
                    handleRegisterAutoBid(request);
                    break;
                case AUTO_BID_CANCEL:
                    handleCancelAutoBid(request);
                    break;
                case DELETE_AUCTION_REQUEST:
                    handleDeleteAuction(request);
                    break;
                default:
                    Message unknownMsg = new Message(MessageType.ERROR);
                    unknownMsg.put("reason", "UNKNOWN_COMMAND");
                    sendMessage(unknownMsg);
                    break;
            }
        } catch (JsonSyntaxException e) {
            Message errorMsg = new Message(MessageType.ERROR);
            errorMsg.put("reason", "Invalid JSON format");
            sendMessage(errorMsg);
        } catch (Exception e) {
            Message errorMsg = new Message(MessageType.ERROR);
            errorMsg.put("reason", e.getMessage());
            sendMessage(errorMsg);
        }
    }

    private void handleRegister(Message request) {
        String username = request.get("username");
        String password = request.get("password");
        String email = request.get("email");
        String role = request.get("role");
        String fullName = request.get("fullName");

        Message response = new Message(MessageType.REGISTER_RESPONSE);

        if (userDAO.getUserByUsername(username) != null) {
            response.put("status", "FAILED");
            response.put("reason", "Tên người dùng đã tồn tại!");
            sendMessage(response);
            return;
        }

        String id = UUID.randomUUID().toString();
        User newUser;
        if ("SELLER".equalsIgnoreCase(role)) {
            newUser = new Seller(id, username, password, email, username + "'s Store");
        } else if ("ADMIN".equalsIgnoreCase(role)) {
            newUser = new Admin(id, username, password, email, "General", "SUPPORT");
        } else {
            newUser = new Bidder(id, username, password, email, 10000000.0); // Default 10M balance
        }

        userDAO.createUser(newUser);
        response.put("status", "SUCCESS");
        sendMessage(response);
    }

    private void handleLogin(Message request) {
        String username = request.get("userId"); // The client calls username 'userId'
        String password = request.get("password");

        Message response = new Message(MessageType.LOGIN_RESPONSE);

        User user = userDAO.getUserByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            response.put("status", "FAILED");
            response.put("reason", "Sai tài khoản hoặc mật khẩu!");
            sendMessage(response);
            return;
        }

        this.userId = user.getId();
        response.put("status", "SUCCESS");
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("role", user.getRole());
        
        if (user instanceof Bidder) {
            response.put("balance", String.valueOf(((Bidder) user).getAccountBalance()));
        } else {
            response.put("balance", "0");
        }

        sendMessage(response);
    }
 
    private void handleForgotPassword(Message request) {
        String username = request.get("username");
        String email = request.get("email");
        String newPassword = request.get("newPassword");

        Message response = new Message(MessageType.FORGOT_PASSWORD_RESPONSE);

        User user = userDAO.getUserByUsername(username);
        if (user == null || !email.equalsIgnoreCase(user.getEmail())) {
            response.put("status", "FAILED");
            response.put("reason", user == null ? "Tên người dùng không tồn tại!" : "Email khôi phục không khớp!");
            sendMessage(response);
            return;
        }

        user.setPassword(newPassword);
        userDAO.updateUser(user);
        response.put("status", "SUCCESS");
        sendMessage(response);
    }

    private void handleDeposit(Message request) {
        Message response = new Message(MessageType.DEPOSIT_RESPONSE);
        if (this.userId == null) {
            response.put("status", "FAILED");
            response.put("reason", "Vui lòng đăng nhập trước!");
            sendMessage(response);
            return;
        }

        try {
            double amount = Double.parseDouble(request.get("amount"));
            if (amount <= 0) {
                response.put("status", "FAILED");
                response.put("reason", "Số tiền nạp phải lớn hơn 0!");
                sendMessage(response);
                return;
            }

            User user = userDAO.getUserById(this.userId);
            if (user instanceof Bidder) {
                Bidder bidder = (Bidder) user;
                bidder.addBalance(amount);
                userDAO.updateUser(bidder);
                response.put("status", "SUCCESS");
                response.put("balance", String.valueOf(bidder.getAccountBalance()));
                System.out.println("[Deposit] Success for user: " + this.userId + " | amount=" + amount + " | newBalance=" + bidder.getAccountBalance());
            } else {
                response.put("status", "FAILED");
                response.put("reason", "Chỉ tài khoản Người mua (Bidder) mới có thể nạp tiền!");
            }
            sendMessage(response);
        } catch (Exception e) {
            response.put("status", "FAILED");
            response.put("reason", "Lỗi nạp tiền: " + e.getMessage());
            sendMessage(response);
        }
    }

    private void handleGetAuctions(Message request) {
        List<Auction> auctions = auctionDAO.getAllAuctions();
        String auctionsJson = gson.toJson(auctions);

        Message response = new Message(MessageType.GET_AUCTIONS_RESPONSE);
        response.put("auctions", auctionsJson);
        sendMessage(response);
    }

    private void handleGetBidHistory(Message request) {
        String auctionId = request.get("auctionId");
        List<BidTransaction> bids = bidDAO.getBidsByAuctionId(auctionId);
        String bidsJson = gson.toJson(bids);

        Message response = new Message(MessageType.GET_BID_HISTORY_RESPONSE);
        response.put("bids", bidsJson);
        sendMessage(response);
    }

    private void handleCreateItem(Message request) {
        Message response = new Message(MessageType.CREATE_ITEM_RESPONSE);
        if (this.userId == null) {
            response.put("status", "FAILED");
            response.put("reason", "Must login first");
            sendMessage(response);
            return;
        }

        try {
            String name = request.get("name");
            String description = request.get("description");
            double startingPrice = Double.parseDouble(request.get("startingPrice"));
            String itemType = request.get("itemType").toUpperCase();
            LocalDateTime startTime = LocalDateTime.parse(request.get("startTime"));
            LocalDateTime endTime = LocalDateTime.parse(request.get("endTime"));

            String itemId = UUID.randomUUID().toString();
            Item item;
            if ("ELECTRONICS".equals(itemType)) {
                item = new Electronics(itemId, name, description, startingPrice, this.userId);
            } else if ("ART".equals(itemType)) {
                item = new Art(itemId, name, description, startingPrice, this.userId);
            } else {
                item = new Vehicle(itemId, name, description, startingPrice, this.userId);
            }

            itemDAO.createItem(item);

            String auctionId = UUID.randomUUID().toString();
            Auction auction = new Auction(auctionId, itemId, this.userId, startTime, endTime);
            auction.setCurrentHighestBid(startingPrice);
            auction.setStartingPrice(startingPrice);
            
            // Determine initial status based on times
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(endTime)) {
                auction.setStatus("FINISHED");
            } else if (now.isAfter(startTime)) {
                auction.setStatus("RUNNING");
            } else {
                auction.setStatus("OPEN");
            }

            auctionDAO.createAuction(auction);

            response.put("status", "SUCCESS");
            sendMessage(response);

            // Broadcast that a new auction has been created
            Message listUpdate = new Message(MessageType.GET_AUCTIONS_RESPONSE);
            listUpdate.put("auctions", gson.toJson(auctionDAO.getAllAuctions()));
            NotificationManager.getInstance().broadcast(listUpdate);

        } catch (Exception e) {
            response.put("status", "FAILED");
            response.put("reason", e.getMessage());
            sendMessage(response);
        }
    }

    private void handlePlaceBid(Message request) {
        if (this.userId != null) {
            String auctionId = request.get("auctionId");
            double amount = Double.parseDouble(request.get("amount"));
            
            // Check bidder balance
            User user = userDAO.getUserById(this.userId);
            if (user instanceof Bidder) {
                Bidder bidder = (Bidder) user;
                if (!bidder.hasEnoughBalance(amount)) {
                    Message bidResp = new Message(MessageType.BID_RESPONSE);
                    bidResp.put("auctionId", auctionId);
                    bidResp.put("status", "FAILED");
                    bidResp.put("reason", "Số dư tài khoản không đủ! (Số dư: " + String.format("%,.0f ₫", bidder.getAccountBalance()) + ")");
                    sendMessage(bidResp);
                    return;
                }
            }

            boolean success = AuctionEngine.getInstance().placeBid(auctionId, this.userId, amount);
            Message bidResp = new Message(MessageType.BID_RESPONSE);
            bidResp.put("auctionId", auctionId);
            if (success) {
                bidResp.put("status", "SUCCESS");
                
                // Refresh balance if bidder
                User updatedUser = userDAO.getUserById(this.userId);
                if (updatedUser instanceof Bidder) {
                    bidResp.put("balance", String.valueOf(((Bidder) updatedUser).getAccountBalance()));
                }
            } else {
                bidResp.put("status", "FAILED");
                bidResp.put("reason", "Giá đấu không hợp lệ hoặc phiên đấu giá đã đóng.");
            }
            sendMessage(bidResp);
        } else {
            Message errorMsg = new Message(MessageType.ERROR);
            errorMsg.put("reason", "Must login first");
            sendMessage(errorMsg);
        }
    }

    private void handleRegisterAutoBid(Message request) {
        Message response = new Message(MessageType.AUTO_BID_RESPONSE);
        if (this.userId == null) {
            response.put("status", "FAILED");
            response.put("reason", "Must login first");
            sendMessage(response);
            return;
        }

        try {
            String auctionId = request.get("auctionId");
            double maxBid = Double.parseDouble(request.get("maxBid"));
            double increment = Double.parseDouble(request.get("increment"));

            AutoBidConfig config = new AutoBidConfig(this.userId, auctionId, maxBid, increment);
            AutoBidManager.getInstance().register(config);

            response.put("status", "SUCCESS");
            response.put("auctionId", auctionId);
            sendMessage(response);

            // Trigger immediately in case they are eligible now
            AutoBidManager.getInstance().triggerAutoBids(auctionId);

        } catch (Exception e) {
            response.put("status", "FAILED");
            response.put("reason", e.getMessage());
            sendMessage(response);
        }
    }

    private void handleCancelAutoBid(Message request) {
        if (this.userId != null) {
            String auctionId = request.get("auctionId");
            AutoBidManager.getInstance().unregister(auctionId, this.userId);
            System.out.println("[AutoBid] Canceled for user: " + this.userId + " on auction: " + auctionId);
        }
    private void handleDeleteAuction(Message request) {
        Message response = new Message(MessageType.DELETE_AUCTION_RESPONSE);
        if (this.userId == null) {
            response.put("status", "FAILED");
            response.put("reason", "Vui lòng đăng nhập trước!");
            sendMessage(response);
            return;
        }

        // Verify role is ADMIN
        User user = userDAO.getUserById(this.userId);
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            response.put("status", "FAILED");
            response.put("reason", "Chỉ Quản trị viên mới có quyền hủy phiên đấu giá!");
            sendMessage(response);
            return;
        }

        try {
            String auctionId = request.get("auctionId");
            auctionDAO.deleteAuction(auctionId);
            response.put("status", "SUCCESS");
            response.put("auctionId", auctionId);
            sendMessage(response);

            // Broadcast that an auction was deleted to refresh all active clients
            Message listUpdate = new Message(MessageType.GET_AUCTIONS_RESPONSE);
            listUpdate.put("auctions", gson.toJson(auctionDAO.getAllAuctions()));
            NotificationManager.getInstance().broadcast(listUpdate);

            System.out.println("[Admin Override] Auction deleted by Admin: " + auctionId);
        } catch (Exception e) {
            response.put("status", "FAILED");
            response.put("reason", "Lỗi hủy phiên: " + e.getMessage());
            sendMessage(response);
        }
    }

    public void sendMessage(Message message) {
        if (out != null) {
            out.println(gson.toJson(message));
        }
    }

    public String getUserId() {
        return userId;
    }

    private void disconnect() {
        NotificationManager.getInstance().unregisterClient(this);
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
            System.out.println("Client disconnected.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
