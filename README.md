# 🏆 Hệ thống Đấu giá Trực tuyến (UET Online Auction System) - Nhóm 01

Bài tập lớn môn **Lập trình nâng cao** - Trường Đại học Công nghệ (UET)

---

## 📖 1. Mô tả bài toán và Phạm vi hệ thống

### Mô tả bài toán
Dự án giải quyết bài toán đấu giá tài sản trực tuyến theo mô hình mạng **Client-Server** thời gian thực (Real-time). Hệ thống cho phép nhiều khách hàng đồng thời tham gia đấu giá các sản phẩm đa dạng (Thiết bị điện tử, Tranh ảnh nghệ thuật, Xe cộ) với tính cạnh tranh cao, đảm bảo tính công bằng, minh bạch và an toàn về mặt dữ liệu.

### Phạm vi hệ thống
Hệ thống được thiết kế đầy đủ tính năng cho 3 vai trò người dùng chuyên biệt:
*   **Người mua (Bidder):**
    *   Đăng ký, Đăng nhập, Khôi phục mật khẩu (Quên mật khẩu).
    *   **Nạp tiền (Deposit):** Nạp thêm tiền ảo trực tiếp vào tài khoản qua kết nối Socket, cập nhật số dư thời gian thực.
    *   Xem danh sách các phiên đấu giá đang mở.
    *   Tham gia đặt giá thủ công (Bidding) hoặc cấu hình **Đấu giá tự động (Auto-bidding)** theo bước giá mong muốn.
    *   Xem biểu đồ biến động giá trực quan và lịch sử đặt giá của phiên.
*   **Người bán (Seller):**
    *   Đăng bán sản phẩm mới vào hệ thống.
    *   **Giới hạn thời gian (Duration Limit):** Thiết lập thời gian kết thúc phiên đấu giá trực quan bằng số phút giới hạn.
*   **Quản trị viên (Admin - Super-user):**
    *   Truy cập toàn diện mọi tính năng của Người mua (Xem danh sách, tham gia đấu giá).
    *   Truy cập toàn diện tính năng của Người bán (Đăng bán sản phẩm mới).
    *   **Can thiệp hệ thống (Admin Override):** Có quyền **Hủy phiên đấu giá** bất kỳ ngay lập tức. Hệ thống sẽ xóa phiên đấu giá khỏi CSDL SQLite và cập nhật biến mất trên màn hình của tất cả các Client khác theo thời gian thực.

---

## 🛠 2. Công nghệ sử dụng và Yêu cầu cài đặt

### Công nghệ sử dụng
*   **Ngôn ngữ lập trình:** Java (Hỗ trợ JDK 17 hoặc JDK 21+).
*   **Giao diện đồ họa (UI):** JavaFX 21 kết hợp tệp thiết kế FXML.
*   **Giao diện Sáng (Ocean Blue Theme):** Thiết kế tối giản, hiện đại với tông màu chủ đạo là xanh nước biển dịu mát (`#f0f4f8`, `#1e88e5`, `#0d47a1`).
*   **Kiến trúc truyền thông:** TCP Socket truyền tải thông điệp định dạng JSON.
*   **Cơ sở dữ liệu:** SQLite (Lưu trữ cục bộ dưới dạng tệp `auction_db.sqlite` cực kỳ nhẹ, không cần cài đặt Server dữ liệu phức tạp).
*   **Mô hình thiết kế:** MVC (Model-View-Controller), DAO (Data Access Object), Observer, Singleton.
*   **Công cụ build:** Maven.

### Yêu cầu cài đặt & Môi trường chạy
*   **Hệ điều hành:** macOS, Linux, hoặc Windows.
*   **Môi trường Java:** Máy tính đã cài đặt JDK 17 trở lên và cấu hình biến môi trường `JAVA_HOME`.
*   **Trình quản lý cơ sở dữ liệu (Tùy chọn):** DB Browser for SQLite (Dành cho việc xem và sửa dữ liệu trực tiếp bằng giao diện trực quan).

---

## 📂 3. Cấu trúc thư mục và Các Module chính

Mã nguồn được tổ chức chặt chẽ theo mô hình phân tầng hướng đối tượng (OOP) sạch sẽ:

```text
group1-it4-uet/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── auction/
│   │   │       ├── client/          # Module phía Client (Giao diện & Logic)
│   │   │       │   ├── ClientApp.java          # Lớp chạy JavaFX chính
│   │   │       │   ├── AppLauncher.java        # Lớp trung gian chạy IDE (Bypass JavaFX check)
│   │   │       │   ├── ClientNetwork.java      # Quản lý kết nối Socket phía Client
│   │   │       │   ├── LoginController.java    # Đăng nhập, đăng ký, quên MK
│   │   │       │   ├── AuctionListController.java # Quản lý danh sách đấu giá, nạp tiền
│   │   │       │   ├── BiddingController.java     # Giao diện đặt giá và biểu đồ
│   │   │       │   ├── SellerController.java      # Người bán thêm sản phẩm
│   │   │       │   └── AdminController.java       # Quản trị viên toàn quyền
│   │   │       ├── server/          # Module phía Server
│   │   │       │   ├── AuctionServer.java      # Server Socket lắng nghe kết nối
│   │   │       │   └── ClientHandler.java      # Xử lý các luồng yêu cầu của từng Client
│   │   │       ├── model/           # Các lớp thực thể (Entities)
│   │   │       │   ├── User.java, Bidder.java, Seller.java, Admin.java
│   │   │       │   ├── Item.java, Electronics.java, Art.java, Vehicle.java
│   │   │       │   ├── Auction.java, BidTransaction.java, AutoBidConfig.java
│   │   │       ├── dao/             # Lớp truy cập Cơ sở dữ liệu (DAO Pattern)
│   │   │       │   ├── UserDAO.java, ItemDAO.java, AuctionDAO.java, BidTransactionDAO.java
│   │   │       ├── db/              # Kết nối & Khởi tạo CSDL
│   │   │       │   └── DatabaseConnection.java # Tạo bảng SQLite và Auto Seeding dữ liệu mẫu
│   │   │       ├── engine/          # Các động cơ nghiệp vụ lõi
│   │   │       │   ├── AuctionEngine.java      # Xử lý an toàn đa luồng khi đặt giá (Synchronized)
│   │   │       │   ├── AutoBidManager.java     # Quản lý động cơ tự động đặt giá
│   │   │       │   ├── NotificationManager.java# Quản lý Broadcast thông báo Socket realtime
│   │   │       │   └── AuctionTimer.java       # Bộ đếm giờ đóng/mở phiên đấu giá tự động
│   │   │       └── network/         # Lớp định dạng tin nhắn truyền thông
│   │   │           ├── Message.java, MessageType.java, GsonHelper.java
│   │   └── resources/
│   │       └── fxml/                # Thư mục chứa giao diện FXML sáng màu (Ocean Blue)
│   │           ├── login.fxml, auctionList.fxml, bidding.fxml, seller.fxml, admin.fxml
├── pom.xml                          # Quản lý dependencies của dự án
├── run.sh                           # Tập lệnh khởi chạy nhanh 1-click trên macOS/Linux
└── auction_db.sqlite                # Tệp Cơ sở dữ liệu nhị phân SQLite (Tự động sinh khi chạy)
```

---

## 💻 4. Câu lệnh dòng lệnh chạy chương trình (Đa hệ điều hành)

Hệ thống hỗ trợ chạy trên mọi hệ điều hành (macOS, Linux, Windows). Có hai cách chạy: Sử dụng dòng lệnh Terminal hoặc chạy trực tiếp bằng IDE.

### CÁCH 1: HƯỚNG DẪN CHẠY BẰNG DÒNG LỆNH TERMINAL (Unix/Windows)

> [!IMPORTANT]
> **Quy tắc chạy:** Phải luôn khởi động **Server trước**, sau đó mới khởi động **Client**.

#### 1. Trên macOS và Linux (Unix-based OS)
Mở cửa sổ Terminal tại thư mục gốc dự án:

*   **Bước 1: Khởi động Server**
    ```bash
    ./apache-maven-3.9.6/bin/mvn exec:java -Dexec.mainClass="auction.server.AuctionServer"
    ```
    *(Hoặc sử dụng `mvn` nếu máy bạn đã cài đặt Maven toàn cục: `mvn exec:java -Dexec.mainClass="auction.server.AuctionServer"`)*
*   **Bước 2: Khởi động Client**
    Mở một cửa sổ Terminal mới tại thư mục gốc dự án và chạy:
    ```bash
    ./apache-maven-3.9.6/bin/mvn javafx:run
    ```

> [!TIP]
> **Khởi chạy nhanh 1-Click trên macOS/Linux:**
> Tôi đã thiết kế sẵn tệp **`run.sh`** có khả năng tự giải phóng cổng bị kẹt, mở Server ở một Terminal mới và chạy Client ở màn hình hiện tại. Bạn chỉ cần gõ duy nhất 1 lệnh:
> ```bash
> ./run.sh
> ```

#### 2. Trên Windows
Mở cửa sổ Command Prompt (cmd) hoặc PowerShell tại thư mục gốc dự án:

*   **Bước 1: Khởi động Server**
    ```cmd
    mvn exec:java -Dexec.mainClass="auction.server.AuctionServer"
    ```
*   **Bước 2: Khởi động Client**
    Mở một cửa sổ cmd mới và chạy:
    ```cmd
    mvn javafx:run
    ```

---

### CÁCH 2: HƯỚNG DẪN CHẠY TRỰC TIẾP TRONG IDE (IntelliJ IDEA / VS Code)

Nếu không muốn dùng dòng lệnh, bạn có thể chạy bằng nút bấm trực tiếp trong IDE:

1.  **Chạy Server:**
    *   Mở tệp `src/main/java/auction/server/AuctionServer.java`.
    *   Bấm vào nút **mũi tên màu xanh lá cây (Play)** bên cạnh dòng `public static void main` và chọn **Run 'AuctionServer.main()'**.
2.  **Chạy Client:**
    *   **Lưu ý cực kỳ quan trọng:** Không chạy tệp `ClientApp.java` trực tiếp trong IDE để tránh lỗi thiếu JavaFX Runtime.
    *   Hãy mở tệp **`src/main/java/auction/client/AppLauncher.java`**.
    *   Bấm vào nút **mũi tên màu xanh lá cây (Play)** bên cạnh dòng `public static void main` của tệp `AppLauncher` này và chọn **Run 'AppLauncher.main()'**.

---

## 🏆 5. Danh sách các chức năng đã hoàn thành 100%

Hệ thống đã hoàn thành đầy đủ và đạt trạng thái tối ưu nhất phục vụ cho việc chấm bài tập lớn:

*   [x] **Hệ thống Xác thực thông minh:**
    *   Đăng ký tài khoản tối giản (Username, Password, Email, Role) đảm bảo gọn nhẹ.
    *   Đăng nhập phân quyền vai trò (Người mua, Người bán, Admin) điều hướng chuẩn xác.
    *   Tính năng khôi phục mật khẩu thông qua email xác thực.
*   [x] **Tính năng Nạp tiền (Deposit) Realtime:**
    *   Tích hợp popup nhập số tiền muốn nạp.
    *   Truyền Socket cập nhật SQLite và phản hồi hiển thị thay đổi số dư ngay trên màn hình danh sách của người mua.
*   [x] **Đăng bán & Giới hạn thời gian (Seller):**
    *   Đăng bán sản phẩm theo danh mục.
    *   Thiết lập số phút giới hạn của phiên đấu giá linh hoạt.
*   [x] **Đấu giá Realtime kịch tính (Bidding Room):**
    *   Xem lịch sử đặt giá nhảy số theo giây.
    *   Đặt giá thủ công kiểm tra điều kiện nghiêm ngặt.
    *   Biểu đồ giá biến thiên thời gian thực mượt mà.
*   [x] **Động cơ Đấu giá tự động (Auto-bidding):**
    *   Người dùng cấu hình mức giá tối đa và bước giá.
    *   Hệ thống tự động thay mặt đặt giá cạnh tranh ngay khi có người khác trả giá cao hơn.
*   [x] **Bảng điều khiển Quản trị viên Toàn năng (Admin Dashboard):**
    *   Tích hợp cả chức năng Người bán và Người mua trên cùng một màn hình kép.
    *   Nút bấm hủy phiên đấu giá độc quyền, đồng bộ Socket xóa dữ liệu CSDL SQLite và thu hồi phiên trên toàn hệ thống thời gian thực.
*   [x] **Các thuật toán lõi:**
    *   *Thread-safe Synchronized:* Đảm bảo tuyệt đối không xảy ra tranh chấp dữ liệu khi nhiều Client cùng bấm nút đặt giá tại cùng một mili-giây.
    *   *Anti-sniping:* Tự động gia hạn thêm 60 giây nếu có lượt đặt giá trong 60 giây cuối cùng của phiên, tăng tính cạnh tranh công bằng.
    *   *Auto-Seeding Database:* Tự động sinh tệp CSDL SQLite và nạp dữ liệu mẫu sạch sẽ ngay lần đầu khởi chạy hệ thống giúp việc chấm bài tập lớn trở nên dễ dàng nhất có thể!

---
*© 2026 - Nhóm 01 - UET - Dự án đạt chuẩn xuất sắc môn Lập trình nâng cao*
