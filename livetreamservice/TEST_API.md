# 🎥 Hướng Dẫn Chạy & Test API - LiveStream Service

## 📌 Cấu hình Server
- **Port**: `8083`
- **Base URL**: `http://localhost:8083`
- **LiveKit Cloud**: `wss://livetream-7f2wlo6u.livekit.cloud`

---

## 🚀 Cách 1: Chạy bằng Gradle

### Window PowerShell:
```powershell
cd "d:\microytb\holy_dev - Copy\livetreamservice"
.\gradlew bootRun
```

### Hoặc build jar và chạy:
```powershell
.\gradlew clean build -x test
java -jar build\libs\livetreamservice-0.0.1-SNAPSHOT.jar
```

---

## 🧪 Test API

### 1️⃣ **Tạo Room** 
**Endpoint:** `POST http://localhost:8083/api/livestream/room`

**Request Body:**
```json
{
  "roomName": "my-first-room"
}
```

**PowerShell (curl):**
```powershell
curl -X POST http://localhost:8083/api/livestream/room `
  -H "Content-Type: application/json" `
  -d '{\"roomName\": \"my-first-room\"}'
```

**Response:**
```json
{
  "message": "Room created"
}
```

---

### 2️⃣ **Tạo Token để Join Room**
**Endpoint:** `POST http://localhost:8083/api/livestream/token`

**Request Body:**
```json
{
  "roomName": "my-first-room",
  "identity": "user123"
}
```

**PowerShell (curl):**
```powershell
curl -X POST http://localhost:8083/api/livestream/token `
  -H "Content-Type: application/json" `
  -d '{\"roomName\": \"my-first-room\", \"identity\": \"user123\"}'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "url": "wss://livetream-7f2wlo6u.livekit.cloud"
}
```

---

## 📋 Test bằng Postman

### **1. Tạo Room:**
- **Method**: POST
- **URL**: `http://localhost:8083/api/livestream/room`
- **Headers**: 
  - `Content-Type: application/json`
- **Body** (raw JSON):
  ```json
  {
    "roomName": "test-room-001"
  }
  ```

### **2. Tạo Token:**
- **Method**: POST
- **URL**: `http://localhost:8083/api/livestream/token`
- **Headers**: 
  - `Content-Type: application/json`
- **Body** (raw JSON):
  ```json
  {
    "roomName": "test-room-001",
    "identity": "john_doe"
  }
  ```

---

## 🔧 Kiểm tra Service đang chạy

```powershell
# Kiểm tra port 8083
netstat -ano | findstr :8083

# Hoặc test health
curl http://localhost:8083/actuator/health
```

---

## ⚠️ Lưu ý

1. **Đảm bảo port 8083 không bị chiếm dụng**
2. **LiveKit credentials** đã được cấu hình trong `application.yml`:
   - API Key: `APIroUAKf3K9soB`
   - API Secret: `6RscGpoNsTqpXzaixecf1IDU0y2eYt7fdeR1GVmuOFvA`
3. **Token có hiệu lực 24 giờ** (86400000 ms)

---

## 🎯 Workflow sử dụng

1. **Tạo room** trước bằng API `/room`
2. **Tạo token** cho từng user muốn join bằng API `/token`
3. **Client** dùng token và URL để kết nối LiveKit:
   ```javascript
   const room = new Room();
   await room.connect('wss://livetream-7f2wlo6u.livekit.cloud', token);
   ```

---

## 📦 Dependencies

- Spring Boot 3.2.2
- LiveKit Server SDK 0.6.1
- JJWT 0.12.3 (JWT token generation)
- Lombok

---

## 🐛 Troubleshooting

### Lỗi: Port 8083 already in use
```powershell
# Tìm process đang dùng port 8083
netstat -ano | findstr :8083

# Kill process (thay PID bằng số thực tế)
taskkill /PID <PID> /F
```

### Lỗi: Connection refused
- Kiểm tra service đã chạy chưa
- Kiểm tra firewall không block port 8083

### Lỗi: Invalid token
- Kiểm tra API key/secret trong `application.yml`
- Verify token chưa hết hạn (24h)
