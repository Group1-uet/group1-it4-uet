package com.Auction.Server.DAO;

import java.sql.*;

public class UserDAO {
    // Hàm kiểm tra tài khoản trong Database
    public boolean authenticate(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            return rs.next(); // Trả về true nếu tìm thấy user
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}