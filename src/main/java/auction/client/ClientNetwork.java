package auction.client;

import auction.network.Message;
import com.google.gson.Gson;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class ClientNetwork {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final Gson gson = auction.network.GsonHelper.getGson();
    private Consumer<Message> onMessageReceived;
    private Thread listenerThread;
    private boolean running = false;

    // Current User Session State
    private String currentUserId;
    private String currentUsername;
    private String currentUserRole;
    private double currentUserBalance;

    public boolean connect(Consumer<Message> onMessageReceived) {
        this.onMessageReceived = onMessageReceived;
        if (socket != null && socket.isConnected() && !socket.isClosed()) {
            return true;
        }
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            running = true;

            // Bắt đầu luồng lắng nghe Server
            listenerThread = new Thread(this::listenForMessages);
            listenerThread.setDaemon(true);
            listenerThread.start();

            return true;
        } catch (IOException e) {
            System.err.println("Không thể kết nối tới Server: " + e.getMessage());
            return false;
        }
    }

    public void sendMessage(Message message) {
        if (out != null) {
            out.println(gson.toJson(message));
        }
    }

    private void listenForMessages() {
        try {
            String line;
            while (running && (line = in.readLine()) != null) {
                final String json = line;
                Message msg = gson.fromJson(json, Message.class);
                if (msg != null && onMessageReceived != null) {
                    // Cập nhật UI phải được thực hiện trên JavaFX Application Thread
                    Platform.runLater(() -> onMessageReceived.accept(msg));
                }
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("Mất kết nối tới Server: " + e.getMessage());
            }
        }
    }

    public void setOnMessageReceived(Consumer<Message> onMessageReceived) {
        this.onMessageReceived = onMessageReceived;
    }

    public void disconnect() {
        running = false;
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        socket = null;
        out = null;
        in = null;
    }

    // Session getters and setters
    public String getCurrentUserId() { return currentUserId; }
    public void setCurrentUserId(String currentUserId) { this.currentUserId = currentUserId; }

    public String getCurrentUsername() { return currentUsername; }
    public void setCurrentUsername(String currentUsername) { this.currentUsername = currentUsername; }

    public String getCurrentUserRole() { return currentUserRole; }
    public void setCurrentUserRole(String currentUserRole) { this.currentUserRole = currentUserRole; }

    public double getCurrentUserBalance() { return currentUserBalance; }
    public void setCurrentUserBalance(double currentUserBalance) { this.currentUserBalance = currentUserBalance; }
}
