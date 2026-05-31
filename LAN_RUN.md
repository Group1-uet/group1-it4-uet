# Chay nhieu may trong cung mang LAN

App can mot may chay Server chung. Tat ca Client phai ket noi ve IP cua may Server do, khong dung `localhost` tren may khac.

## 1. Chay Server

Tren may lam Server:

```powershell
.\apache-maven-3.9.6\bin\mvn.cmd exec:java "-Dexec.mainClass=auction.server.AuctionServer"
```

Lay dia chi IPv4 cua may Server:

```powershell
ipconfig
```

Vi du IP Server la `192.168.1.10`.

## 2. Chay Client tren may khac

PowerShell:

```powershell
$env:AUCTION_SERVER_HOST="192.168.1.10"
$env:AUCTION_SERVER_PORT="8080"
.\apache-maven-3.9.6\bin\mvn.cmd javafx:run
```

Command Prompt:

```cmd
set AUCTION_SERVER_HOST=192.168.1.10
set AUCTION_SERVER_PORT=8080
.\apache-maven-3.9.6\bin\mvn.cmd javafx:run
```

Khi tat ca Client ket noi cung mot Server, vat pham/phien dau gia moi se duoc luu vao `auction_db.sqlite` tren may Server va duoc cap nhat toi cac Client dang mo danh sach dau gia.

Neu may khac khong ket noi duoc, kiem tra firewall cua may Server va mo cong TCP `8080`.
