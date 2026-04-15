# 🔨 Hệ thống đấu giá trực tuyến - Team 01 (UET)

<p align="center">
  <img src="https://img.shields.io/badge/JAVA-17+-orange?style=for-the-badge&logo=java" />
  <img src="https://img.shields.io/badge/JAVAFX-17-blue?style=for-the-badge&logo=java" />
  <img src="https://img.shields.io/badge/ARCHITECTURE-CLIENT--SERVER-yellow?style=for-the-badge" />
  <img src="https://img.shields.io/badge/BUILD-MAVEN-red?style=for-the-badge&logo=apache-maven" />
</p>

---

## 📖 Giới thiệu dự án
Dự án được phát triển cho môn **Lập trình nâng cao** tại **Đại học Công nghệ**. Hệ thống cho phép nhiều người dùng tham gia đấu giá sản phẩm theo thời gian thực, áp dụng các nguyên lý lập trình hướng đối tượng (**OOP**) và mô hình **MVC**.

## 👥 Thành viên nhóm
| STT | Họ và tên | MSSV | Vai trò |
| :--- | :--- | :--- | :--- |
| 1 | **Nguyễn Ngọc Linh** | 22028212 | Leader / Backend |
| 2 | **Nguyễn Bá Thủy** | 22028218 | Frontend / JavaFX |
| 3 | **Bùi Minh Lâm** | 22028230 | Developer / Database |

## 🏗 Kiến trúc Hệ thống
Hệ thống tuân thủ mô hình phân tầng để tách biệt giao diện, nghiệp vụ và dữ liệu:
* **Kiến trúc:** Client-Server kết nối qua Socket (dữ liệu định dạng JSON).
* **Client-side:** JavaFX + FXML áp dụng mô hình MVC.
* **Server-side:** Xử lý đa luồng (Multi-threading), quản lý phiên đấu giá và kết nối Database.

## ✨ Tính năng nổi bật
* **Quản lý phiên đấu giá:** Tự động mở/đóng phiên theo thời gian thực.
* **Đấu giá đồng thời:** Xử lý an toàn khi nhiều người cùng đặt giá tại một thời điểm (Concurrency).
* **Cập nhật Realtime:** Sử dụng Observer Pattern để cập nhật giá ngay lập tức cho tất cả client.
* **Chức năng nâng cao:** Đấu giá tự động (Auto-bidding) và thuật toán gia hạn phiên (Anti-sniping).

## 🛠 Yêu cầu hệ thống
* **Java:** Version 17 trở lên.
* **Build tool:** Maven.
* **Thư viện:** JavaFX, Jackson (xử lý JSON), JUnit (kiểm thử).

---
*© 2026 - Team 01 - UET - Bài tập lớn Lập trình nâng cao*
