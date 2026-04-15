# 🔨 Hệ thống đấu giá trực tuyến - Team 01 (UET)

<p align="center">
  <img src="https://img.shields.io/badge/JAVA-17+-orange?style=for-the-badge&logo=java" />
  <img src="https://img.shields.io/badge/JAVAFX-17-blue?style=for-the-badge&logo=java" />
  <img src="https://img.shields.io/badge/ARCHITECTURE-CLIENT--SERVER-yellow?style=for-the-badge" />
  <img src="https://img.shields.io/badge/BUILD-MAVEN-red?style=for-the-badge&logo=apache-maven" />
</p>

---

## 📖 Giới thiệu dự án
[cite_start]Dự án được phát triển cho môn **Lập trình nâng cao** tại **Đại học Công nghệ**[cite: 7, 8]. [cite_start]Hệ thống cho phép nhiều người dùng tham gia đấu giá sản phẩm theo thời gian thực [cite: 23, 25, 47][cite_start], áp dụng các nguyên lý **OOP** và mô hình **MVC**[cite: 9, 128, 129].

## 👥 Thành viên nhóm
| STT | Họ và tên                                     | MSSV                         | Vai trò                      |
| :--- |:----------------------------------------------|:-----------------------------|:-----------------------------|
| 1 | **Nguyễn Ngọc Linh**                          | [MSSV]                       | Tester / Backend / Document  |
| 2 | **Nguyễn Bá Thủy**                            | [MSSV]                       | Frontend / JavaFX            |
| 3 | **Bùi Minh Lâm**                              | [MSSV]                       | Leader / Developer / Database |
## 🏗 Kiến trúc Hệ thống
[cite_start]Hệ thống tuân thủ mô hình phân tầng để tách biệt giao diện, nghiệp vụ và dữ liệu[cite: 124]:
* [cite_start]**Kiến trúc:** Client-Server kết nối qua Socket (JSON data)[cite: 125, 126].
* [cite_start]**Client-side:** JavaFX + FXML áp dụng mô hình MVC[cite: 62, 63, 128].
* [cite_start]**Server-side:** Xử lý đa luồng, quản lý phiên đấu giá và kết nối Database[cite: 129, 130].

## ✨ Tính năng nổi bật
* [cite_start]**Quản lý phiên đấu giá:** Tự động mở/đóng phiên theo thời gian[cite: 52, 53].
* [cite_start]**Đấu giá đồng thời:** Xử lý nhiều người cùng bid giá tại một thời điểm (Concurrency)[cite: 83, 84].
* [cite_start]**Cập nhật Realtime:** Sử dụng Observer Pattern để cập nhật giá ngay lập tức cho tất cả client[cite: 94, 95, 143].
* [cite_start]**Chức năng nâng cao:** Đấu giá tự động (Auto-bidding) và Gia hạn phiên (Anti-sniping)[cite: 72, 89, 90].

## 🛠 Yêu cầu hệ thống
* **Java:** version 17 trở lên.
* [cite_start]**Build tool:** Maven.
* [cite_start]**Thư viện:** JavaFX, Jackson (xử lý JSON), JUnit (kiểm thử)[cite: 63, 126, 136].

---
*© 2026 - Team 01 - UET - Lập trình nâng cao*