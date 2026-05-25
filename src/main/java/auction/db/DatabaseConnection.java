package auction.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:auction_db.sqlite";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
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
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
