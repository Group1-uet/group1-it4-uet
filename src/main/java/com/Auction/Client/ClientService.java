package com.Auction.Client;

import com.Auction.Common.Message;
import com.google.gson.Gson;
import java.io.*;
import java.net.Socket;

public class ClientService {
    private static ClientService instance;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Gson gson = new Gson();

    public static ClientService getInstance() {
        if (instance == null) instance = new ClientService();
        return instance;
    }

    public void connect() throws IOException {
        this.socket = new Socket("localhost", 1234);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Client: Đã kết nối tới Server thành công!");
    }

    public void sendRequest(String type, String data) {
        Message msg = new Message(type, data);
        out.println(gson.toJson(msg));
    }

    // --- ĐÂY LÀ PHƯƠNG THỨC MỚI THÊM VÀO ---
    public String sendLoginRequest(String username, String password) {
        try {
            // 1. Gửi yêu cầu đăng nhập
            Message loginMsg = new Message("LOGIN", username + "|" + password);
            out.println(gson.toJson(loginMsg));

            // 2. Nhận phản hồi từ Server
            String responseJson = in.readLine();
            Message res = gson.fromJson(responseJson, Message.class);

            // Trả về kết quả (LOGIN_SUCCESS hoặc LOGIN_FAIL)
            return res.getType();
        } catch (IOException e) {
            System.err.println("Lỗi gửi yêu cầu đăng nhập: " + e.getMessage());
            return "ERROR";
        }
    }

    public static void main(String[] args) {
        try {
            ClientService.getInstance().connect();
            // Test thử hàm đăng nhập mới
            String result = ClientService.getInstance().sendLoginRequest("testuser", "123456");
            System.out.println("Kết quả đăng nhập thử: " + result);
        } catch (IOException e) {
            System.out.println("Không thể kết nối đến Server.");
        }
    }
}