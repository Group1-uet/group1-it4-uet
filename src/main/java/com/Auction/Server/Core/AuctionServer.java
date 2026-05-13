package com.Auction.Server.Core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AuctionServer {
    private static final int PORT = 1234;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println(">>> Auction Server đang chạy tại Port: " + PORT);

            while (true) {
                // Đợi Client kết nối
                Socket clientSocket = serverSocket.accept();
                System.out.println(">>> Có kết nối mới từ: " + clientSocket.getInetAddress());

                // Chuyển việc xử lý cho ClientHandler trong một luồng riêng
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.err.println("Lỗi Server: " + e.getMessage());
        }
    }
}
