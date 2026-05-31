#!/bin/bash

# Thiết lập mã màu ANSI cho Terminal đẹp mắt
BLUE='\033[0;34m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}===================================================${NC}"
echo -e "${BLUE}⚡ HỆ THỐNG ĐẤU GIÁ NHANH - NHÓM 01 (UET AUCTION)   ${NC}"
echo -e "${BLUE}===================================================${NC}"

# 1. Tự động tìm và giải phóng cổng 8080 nếu bị kẹt
echo -e "${YELLOW}🔍 Bước 1: Đang kiểm tra cổng mạng 8080...${NC}"
PID=$(lsof -t -i:8080)
if [ ! -z "$PID" ]; then
    echo -e "${RED}⚠️ Phát hiện cổng 8080 đang bị chiếm dụng bởi tiến trình (PID: $PID).${NC}"
    echo -e "${RED}⏳ Đang tự động giải phóng cổng để tránh lỗi kẹt Server...${NC}"
    kill -9 $PID
    sleep 1.5
    echo -e "${GREEN}✅ Giải phóng cổng thành công!${NC}"
else
    echo -e "${GREEN}✅ Cổng 8080 đã sạch sẽ và sẵn sàng!${NC}"
fi

# Thư mục hiện tại của dự án
DIR="/Users/admin/group1-it4-uet"

# 2. Mở một cửa sổ Terminal mới chạy Server tự động
echo -e "${YELLOW}⏳ Bước 2: Đang khởi động Server ở cửa sổ riêng biệt...${NC}"
osascript -e "tell application \"Terminal\" to do script \"cd '$DIR' && ./apache-maven-3.9.6/bin/mvn exec:java -Dexec.mainClass=\\\"auction.server.AuctionServer\\\"\""

# Chờ 3 giây để Server khởi tạo CSDL SQLite và sẵn sàng nhận kết nối
echo -e "${YELLOW}⏳ Đang chờ Server khởi động CSDL (3 giây)...${NC}"
sleep 3
echo -e "${GREEN}✅ Server đã sẵn sàng!${NC}"

# 3. Chạy Client ngay tại cửa sổ Terminal hiện tại
echo -e "${BLUE}===================================================${NC}"
echo -e "${GREEN}🚀 Bước 3: Đang khởi chạy ứng dụng Client JavaFX...${NC}"
echo -e "${BLUE}===================================================${NC}"
./apache-maven-3.9.6/bin/mvn javafx:run
