package auction.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static String url = "jdbc:sqlite:auction_db.sqlite";

    public static void setUrl(String newUrl) {
        url = newUrl;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // Enable Foreign Keys in SQLite
            stmt.execute("PRAGMA foreign_keys = ON;");

            // Create users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id TEXT PRIMARY KEY, " +
                    "username TEXT UNIQUE NOT NULL, " +
                    "password TEXT NOT NULL, " +
                    "email TEXT, " +
                    "full_name TEXT, " +
                    "role TEXT CHECK(role IN ('BIDDER', 'SELLER', 'ADMIN')) NOT NULL, " +
                    "balance REAL DEFAULT 10000000.0" +
                    ");");

            // Create items table
            stmt.execute("CREATE TABLE IF NOT EXISTS items (" +
                    "id TEXT PRIMARY KEY, " +
                    "seller_id TEXT, " +
                    "name TEXT NOT NULL, " +
                    "description TEXT, " +
                    "starting_price REAL NOT NULL, " +
                    "item_type TEXT CHECK(item_type IN ('ELECTRONICS', 'ART', 'VEHICLE')) NOT NULL, " +
                    "FOREIGN KEY (seller_id) REFERENCES users(id) ON DELETE CASCADE" +
                    ");");

            // Create auctions table
            stmt.execute("CREATE TABLE IF NOT EXISTS auctions (" +
                    "id TEXT PRIMARY KEY, " +
                    "item_id TEXT, " +
                    "start_time TEXT NOT NULL, " +
                    "end_time TEXT NOT NULL, " +
                    "current_price REAL, " +
                    "status TEXT CHECK(status IN ('OPEN', 'RUNNING', 'FINISHED', 'PAID', 'CANCELED', 'CLOSED')) DEFAULT 'OPEN', " +
                    "FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE" +
                    ");");

            // Create bids table
            stmt.execute("CREATE TABLE IF NOT EXISTS bids (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "auction_id TEXT, " +
                    "bidder_id TEXT, " +
                    "amount REAL NOT NULL, " +
                    "bid_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (auction_id) REFERENCES auctions(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (bidder_id) REFERENCES users(id) ON DELETE CASCADE" +
                    ");");

            System.out.println("SQLite database initialized successfully.");

            // Tự động nạp dữ liệu mẫu (Auto Seeding) nếu cơ sở dữ liệu trống
            try (Statement checkStmt = conn.createStatement();
                 ResultSet checkRs = checkStmt.executeQuery("SELECT COUNT(*) FROM users")) {
                if (checkRs.next() && checkRs.getInt(1) == 0) {
                    stmt.execute("INSERT INTO users (id, username, password, email, full_name, role, balance) VALUES " +
                            "('S001', 'seller1', 'pass123', 'seller@example.com', 'Seller One', 'SELLER', 0.0), " +
                            "('B001', 'bidder1', 'pass123', 'bidder@example.com', 'Bidder One', 'BIDDER', 50000000.0);");
                    
                    stmt.execute("INSERT INTO items (id, seller_id, name, description, starting_price, item_type) VALUES " +
                            "('I001', 'S001', 'iPhone 15 Pro Max', 'Apple iPhone 15 Pro Max 256GB Gold', 25000000.0, 'ELECTRONICS'), " +
                            "('I002', 'S001', 'Bức Tranh Mùa Thu', 'Bức tranh phong cảnh mùa thu Hà Nội', 5000000.0, 'ART');");
                            
                    stmt.execute("INSERT INTO auctions (id, item_id, start_time, end_time, current_price, status) VALUES " +
                            "('A001', 'I001', '2026-05-30T00:00:00', '2026-05-30T23:59:59', 25000000.0, 'RUNNING'), " +
                            "('A002', 'I002', '2026-05-30T00:00:00', '2026-05-30T23:59:59', 5000000.0, 'RUNNING');");
                    System.out.println("SQLite database seeded successfully with mock data.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
