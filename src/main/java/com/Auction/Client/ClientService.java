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

    // Dùng Singleton để mọi Controller đều gọi được chung 1 kết nối
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

    // Hàm main dùng TẠM THỜI để bạn test thử xem Client có gọi được Server không
    public static void main(String[] args) {
        try {
            ClientService.getInstance().connect();
            ClientService.getInstance().sendRequest("TEST_CONNECTION", "Hello Server!");
        } catch (IOException e) {
            System.out.println("Không thể kết nối đến Server. Bạn đã chạy Server chưa?");
        }
    }
}