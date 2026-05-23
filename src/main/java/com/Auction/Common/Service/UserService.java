package com.Auction.Common.Service;

import com.Auction.Common.Models.User.*;
import com.Auction.Common.Exceptions.AuthenticationException;
import java.util.*;

public class UserService {
    private final Map<String, User> users = new HashMap<>();
    private static UserService instance;

    private UserService() {
        // Singleton
    }

    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    /**
     * Đăng ký tài khoản mới
     */
    public User register(String username, String password, String displayName, UserRole role)
            throws IllegalArgumentException {
        if (users.containsKey(username)) {
            throw new IllegalArgumentException("Username already exists!");
        }
        if (password.length() < 3) {
            throw new IllegalArgumentException("Password must be at least 3 characters!");
        }

        User user;
        switch (role) {
            case BIDDER:
                user = new Bidder(displayName, username, password);
                break;
            case SELLER:
                user = new Seller(displayName, username, password);
                break;
            case ADMIN:
                user = new Admin(displayName, username, password);
                break;
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }

        users.put(username, user);
        return user;
    }

    /**
     * Đăng nhập
     */
    public User login(String username, String password) throws AuthenticationException {
        User user = users.get(username);
        if (user == null || !user.getPassword().equals(password)) {
            throw new AuthenticationException("Invalid username or password!");
        }
        return user;
    }

    /**
     * Lấy thông tin người dùng
     */
    public User getUserById(String userId) {
        return users.values().stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Lấy thông tin theo username
     */
    public User getUserByUsername(String username) {
        return users.get(username);
    }

    /**
     * Lấy tất cả người dùng
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    /**
     * Cập nhật thông tin người dùng
     */
    public void updateUser(String userId, String displayName) throws IllegalArgumentException {
        User user = getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found!");
        }
        user.setDisplayName(displayName);
    }

    /**
     * Xóa người dùng (dành cho Admin)
     */
    public void deleteUser(String userId, String adminId) throws AuthenticationException {
        User admin = getUserById(adminId);
        if (!(admin instanceof Admin)) {
            throw new AuthenticationException("Only Admin can delete users!");
        }
        users.values().removeIf(u -> u.getId().equals(userId));
    }

    /**
     * Kiểm tra quyền truy cập
     */
    public boolean hasPermission(User user, String requiredRole) {
        if (user == null) return false;
        return user.getRole().toString().equals(requiredRole);
    }
}