package com.Auction.Server.Core;

import com.Auction.Common.Message;
import com.google.gson.Gson;
import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Gson gson = new Gson();

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String requestJson;
            while ((requestJson = in.readLine()) != null) {
                Message msg = gson.fromJson(requestJson, Message.class);
                System.out.println("Server nhận loại tin: " + msg.getType());

                // Gửi phản hồi giả lập
                Message responseMsg = new Message("RESPONSE", "{\"status\":\"ok\"}");
                out.println(gson.toJson(responseMsg));
            }
        } catch (IOException e) {
            System.out.println("Kết nối bị đóng.");
        } finally {
            try {
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}