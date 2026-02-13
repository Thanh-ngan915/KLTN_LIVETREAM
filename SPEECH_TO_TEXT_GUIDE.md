# 🎤 Hướng Dẫn Sử Dụng Tính Năng Nhận Diện Giọng Nói & Hiển Thị Sản Phẩm

## 🎯 Tính Năng

Trong livestream, người phát có thể:
1. **Nói** về sản phẩm (ví dụ: "quần A có id là 123")
2. Hệ thống tự động **nhận diện giọng nói** → **trích xuất ID sản phẩm**
3. **Hiển thị sản phẩm** cho tất cả người xem trong phòng livestream

## 🚀 Cách Sử Dụng

### Bước 1: Chạy Backend Service

```powershell
cd "d:\microytb\holy_dev - Copy - Copy\livetreamservice"
.\gradlew bootRun
```

Backend sẽ chạy tại: **http://localhost:8084**

### Bước 2: Chạy Frontend

```bash
cd "d:\microytb\holy_dev - Copy - Copy\my-app"
npm run dev
```

Frontend sẽ chạy tại: **http://localhost:3000**

### Bước 3: Vào Phòng Livestream

1. Mở trình duyệt: `http://localhost:3000/livestream`
2. Nhập tên phòng: `test-room`
3. Nhập tên của bạn: `Streamer`
4. Nhấn **"Tham gia phòng"**
5. Cho phép trình duyệt truy cập **Camera** và **Microphone**

### Bước 4: Bật Nhận Diện Giọng Nói

1. Trong phòng livestream, nhấn nút **"Bật nhận diện giọng nói"** (góc trên bên trái)
2. Cho phép trình duyệt truy cập **Microphone** nếu được yêu cầu
3. Nút sẽ chuyển sang màu đỏ với chữ **"Dừng lắng nghe"** và **animation pulse**

### Bước 5: Nói Về Sản Phẩm

Nói các câu như:
- **"quần A có id là 123"**
- **"sản phẩm mã 456"**
- **"áo số 789"**

Hệ thống sẽ:
1. ✅ Nhận diện giọng nói → chuyển thành text
2. ✅ Trích xuất ID sản phẩm (123, 456, 789)
3. ✅ Hiển thị thông tin sản phẩm ở **góc dưới bên phải**
4. ✅ **Broadcast** sản phẩm cho tất cả người xem qua **LiveKit DataChannel**

## 📦 Sản Phẩm Mẫu Có Sẵn

| ID  | Tên Sản Phẩm | Giá        | Mô Tả                    |
|-----|--------------|------------|--------------------------|
| 123 | Quần A       | 450,000₫   | Quần jean nam cao cấp    |
| 456 | Áo B         | 250,000₫   | Áo thun nam trơn         |
| 789 | Giày C       | 1,200,000₫ | Giày thể thao Nike       |

## 🧪 Test Với Nhiều Người Xem

### Cách 1: Mở Nhiều Tab (Đơn giản nhất)

```bash
# Tab 1 - Người phát (Streamer)
http://localhost:3000/livestream
Tên phòng: test-room
Tên: Streamer

# Tab 2 - Người xem 1
http://localhost:3000/livestream
Tên phòng: test-room
Tên: Viewer1

# Tab 3 - Người xem 2
http://localhost:3000/livestream
Tên phòng: test-room
Tên: Viewer2
```

### Cách 2: Nhiều Trình Duyệt

- **Chrome**: Streamer (bật nhận diện giọng nói)
- **Edge**: Viewer1
- **Firefox**: Viewer2

### Cách 3: Nhiều Thiết Bị (Cùng mạng)

1. Tìm IP máy của bạn:
   ```powershell
   ipconfig
   # Tìm IPv4 Address (ví dụ: 192.168.1.100)
   ```

2. Trên điện thoại/máy khác, truy cập:
   ```
   http://192.168.1.100:3000/livestream
   ```

## 🎬 Kịch Bản Demo

### Người Phát (Streamer):

1. Bật camera và mic
2. Bật nhận diện giọng nói
3. Nói: **"Xin chào mọi người, hôm nay tôi sẽ giới thiệu quần A có id là 123"**
4. → Sản phẩm "Quần A" xuất hiện bên phải màn hình
5. Nói tiếp: **"Và đây là áo B mã 456"**
6. → Sản phẩm "Áo B" thay thế

### Người Xem (Viewers):

- Thấy video của Streamer
- **Tự động nhận** thông tin sản phẩm hiển thị (qua DataChannel)
- Không cần bật nhận diện giọng nói

## 🔧 API Endpoints

### 1. Lấy Sản Phẩm Theo ID

```http
GET http://localhost:8084/api/products/123
```

**Response:**
```json
{
  "id": 123,
  "name": "Quần A",
  "description": "Quần jean nam cao cấp",
  "price": 450000.0,
  "imageUrl": "https://via.placeholder.com/300x400",
  "category": "Fashion"
}
```

### 2. Trích Xuất Sản Phẩm Từ Text

```http
POST http://localhost:8084/api/products/extract-from-text
Content-Type: application/json

{
  "text": "quần A có id là 123"
}
```

**Response:**
```json
{
  "productId": 123,
  "product": {
    "id": 123,
    "name": "Quần A",
    "description": "Quần jean nam cao cấp",
    "price": 450000.0,
    "imageUrl": "https://via.placeholder.com/300x400",
    "category": "Fashion"
  },
  "extractedFrom": "quần A có id là 123"
}
```

### 3. Lấy Tất Cả Sản Phẩm

```http
GET http://localhost:8084/api/products
```

## 🎨 Kiến Trúc Hệ Thống

```
🎤 Streamer nói
    ↓
🌐 Web Speech API (Browser STT)
    ↓
📝 Text: "quần A có id là 123"
    ↓
🔍 Frontend extract số: 123
    ↓
📡 POST /api/products/extract-from-text
    ↓
🔎 Backend Regex: \\b([0-9]{3,})\\b
    ↓
📦 Query Product ID = 123
    ↓
📤 Return Product data
    ↓
📺 Display trong UI
    ↓
🚀 LiveKit DataChannel broadcast
    ↓
👥 Tất cả Viewers nhận product
```

## ✨ Tính Năng Nổi Bật

### ✅ Frontend (React/Next.js)
- Speech-to-Text với Web Speech API (hỗ trợ tiếng Việt)
- LiveKit DataChannel để broadcast realtime
- UI/UX đẹp với Tailwind CSS + animations
- Responsive design

### ✅ Backend (Spring Boot)
- REST API với Spring Boot
- Regex pattern matching thông minh
- CORS configuration cho localhost
- Mock database (dễ chuyển sang DB thật)

### ✅ Regex Patterns
Nhận diện được nhiều format:
- "id là 123"
- "mã 456"  
- "số 789"
- Số 3 chữ số trở lên đứng độc lập

## 🛠️ Troubleshooting

### ❌ Không nhận diện được giọng nói

**Nguyên nhân:**
- Trình duyệt không hỗ trợ Web Speech API
- Chưa cho phép microphone

**Giải pháp:**
- Dùng Chrome/Edge (hỗ trợ tốt nhất)
- Cho phép microphone trong browser settings
- Kiểm tra mic hoạt động

### ❌ Không hiển thị sản phẩm

**Nguyên nhân:**
- Backend chưa chạy
- CORS issue
- ID sản phẩm không tồn tại

**Giải pháp:**
- Kiểm tra backend đang chạy tại port 8084
- Kiểm tra Console log trong DevTools
- Thử với ID có sẵn: 123, 456, 789

### ❌ Người xem không thấy sản phẩm

**Nguyên nhân:**
- LiveKit DataChannel chưa kết nối
- Người xem vào phòng trước khi streamer gửi

**Giải pháp:**  
- Đảm bảo cùng phòng livestream
- Streamer nói lại để broadcast

## 📊 Mở Rộng Tương Lai

- [ ] Kết nối database thật (MySQL/PostgreSQL)
- [ ] Thêm nhiều sản phẩm
- [ ] Upload hình ảnh sản phẩm
- [ ] Giỏ hàng trong livestream
- [ ] Thanh toán trực tiếp
- [ ] Chat box để hỏi sản phẩm
- [ ] Analytics số lượt xem sản phẩm

---

**Chúc bạn thành công! 🎉**
