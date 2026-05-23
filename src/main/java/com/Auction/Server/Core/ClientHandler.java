package com.Auction.Server.Core;

import com.Auction.Common.Message;
import com.Auction.Server.DAO.UserDAO;
import com.google.gson.Gson;
import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Gson gson = new Gson();
    private UserDAO userDAO = new UserDAO();

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

                if (msg.getType().equals("LOGIN")) {
                    String[] credentials = msg.getData().split("\\|");
                    String username = credentials[0];
                    String password = credentials[1];

                    boolean isSuccess = userDAO.authenticate(username, password);

                    if (isSuccess) {
                        out.println(gson.toJson(new Message("LOGIN_SUCCESS", username)));
                    } else {
                        out.println(gson.toJson(new Message("LOGIN_FAIL", "Invalid username or password")));
                    }
                } else {
                    out.println(gson.toJson(new Message("RESPONSE", "{\"status\":\"received\"}")));
                }
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