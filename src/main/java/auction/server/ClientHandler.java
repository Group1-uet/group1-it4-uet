package auction.server;

import auction.engine.AuctionEngine;
import auction.engine.NotificationManager;
import auction.network.Message;
import auction.network.MessageType;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String userId;
    private final Gson gson;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.gson = new Gson();
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
                case LOGIN_REQUEST:
                    this.userId = request.get("userId");
                    Message loginResp = new Message(MessageType.LOGIN_RESPONSE);
                    loginResp.put("status", "SUCCESS");
                    loginResp.put("userId", this.userId);
                    sendMessage(loginResp);
                    break;
                case BID_REQUEST:
                    if (this.userId != null) {
                        String auctionId = request.get("auctionId");
                        double amount = Double.parseDouble(request.get("amount"));
                        
                        boolean success = AuctionEngine.getInstance().placeBid(auctionId, this.userId, amount);
                        Message bidResp = new Message(MessageType.BID_RESPONSE);
                        bidResp.put("auctionId", auctionId);
                        if (success) {
                            bidResp.put("status", "SUCCESS");
                        } else {
                            bidResp.put("status", "FAILED");
                            bidResp.put("reason", "Invalid amount or auction closed");
                        }
                        sendMessage(bidResp);
                    } else {
                        Message errorMsg = new Message(MessageType.ERROR);
                        errorMsg.put("reason", "Must login first");
                        sendMessage(errorMsg);
                    }
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
