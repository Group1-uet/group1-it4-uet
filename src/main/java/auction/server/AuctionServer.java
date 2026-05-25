package auction.server;

import auction.engine.AuctionTimer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AuctionServer {
    private static final int PORT = 8080;
    private boolean isRunning = true;
    private AuctionTimer auctionTimer;

    public void startServer() {
        // Khởi động luồng Background Timer tự động đóng phiên đấu giá
        auctionTimer = new AuctionTimer();
        auctionTimer.start();
        System.out.println("Auction Timer started...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Auction Server is running on port " + PORT + "...");

            while (isRunning) {
                // Lắng nghe kết nối tới từ Client
                Socket clientSocket = serverSocket.accept();
                
                // Khởi tạo một luồng riêng để xử lý Client này
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (auctionTimer != null) {
                auctionTimer.stop();
            }
        }
    }

    public void stopServer() {
        this.isRunning = false;
        if (auctionTimer != null) {
            auctionTimer.stop();
        }
    }

    public static void main(String[] args) {
        // Initialize SQLite database schema
        auction.db.DatabaseConnection.initializeDatabase();

        AuctionServer server = new AuctionServer();
        server.startServer();
    }
}
