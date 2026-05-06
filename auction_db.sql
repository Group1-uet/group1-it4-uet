-- Tạo bảng người dùng (Tương ứng với các lớp trong gói Models.User)
CREATE TABLE IF NOT EXISTS users (
                                     id VARCHAR(50) PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    full_name VARCHAR(100),
    role ENUM('BIDDER', 'SELLER', 'ADMIN') NOT NULL
    );

-- Tạo bảng sản phẩm (Tương ứng với lớp Models.Item)
CREATE TABLE IF NOT EXISTS items (
                                     id VARCHAR(50) PRIMARY KEY,
    seller_id VARCHAR(50),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    starting_price DECIMAL(15, 2) NOT NULL,
    item_type VARCHAR(50), -- Lưu loại sản phẩm: Electronics, Art...
    FOREIGN KEY (seller_id) REFERENCES users(id) ON DELETE CASCADE
    );

-- Tạo bảng phiên đấu giá (Tương ứng với gói Models.auction)
CREATE TABLE IF NOT EXISTS auctions (
                                        id VARCHAR(50) PRIMARY KEY,
    item_id VARCHAR(50),
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    current_price DECIMAL(15, 2),
    status ENUM('OPEN', 'RUNNING', 'FINISHED', 'PAID', 'CANCELED') DEFAULT 'OPEN',
    FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE
    );

--  Tạo bảng lịch sử đặt giá (Tương ứng với BidTransaction)
CREATE TABLE IF NOT EXISTS bids (
                                    id INT AUTO_INCREMENT PRIMARY KEY,
                                    auction_id VARCHAR(50),
    bidder_id VARCHAR(50),
    amount DECIMAL(15, 2) NOT NULL,
    bid_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (auction_id) REFERENCES auctions(id) ON DELETE CASCADE,
    FOREIGN KEY (bidder_id) REFERENCES users(id) ON DELETE CASCADE
    );
