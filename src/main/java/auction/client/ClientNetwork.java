package auction.client;

import auction.network.Message;
import auction.network.MessageType;
import com.google.gson.Gson;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * Quản lý kết nối Socket từ Client đến AuctionServer.
 * Chạy một luồng nền để liên tục lắng nghe tin nhắn từ Server.
 */
public class ClientNetwork {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final Gson gson = new Gson();
    private Consumer<Message> onMessageReceived;
    private Thread listenerThread;
    private boolean running = false;

    public boolean connect(String userId, Consumer<Message> onMessageReceived) {
        this.onMessageReceived = onMessageReceived;
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            running = true;

            // Bắt đầu luồng lắng nghe Server
            listenerThread = new Thread(this::listenForMessages);
            listenerThread.setDaemon(true);
            listenerThread.start();

            // Gửi lệnh đăng nhập
            Message loginMsg = new Message(MessageType.LOGIN_REQUEST);
            loginMsg.put("userId", userId);
            sendMessage(loginMsg);

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
    }
}
