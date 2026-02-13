package org.example.livetreamservice.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.livetreamservice.dto.*;
import org.example.livetreamservice.service.LiveKitService;
import org.example.livetreamservice.service.WhisperService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/livestream")
@RequiredArgsConstructor
public class LivestreamController {

    private final LiveKitService liveKitService;
    private final WhisperService whisperService;

    // Store current product for each room (roomName -> productId)
    private final Map<String, Long> roomProducts = new ConcurrentHashMap<>();

    // Mock Products database
    private static final Map<Long, Product> PRODUCTS = new HashMap<>();
    
    static {
        PRODUCTS.put(123L, new Product(123L, "Quần A", "Quần jean nam cao cấp", 450000.0, 
            "https://via.placeholder.com/300x400?text=Quan+A", "Fashion"));
        PRODUCTS.put(456L, new Product(456L, "Áo B", "Áo thun nam trơn", 250000.0, 
            "https://via.placeholder.com/300x400?text=Ao+B", "Fashion"));
        PRODUCTS.put(789L, new Product(789L, "Giày C", "Giày thể thao Nike", 1200000.0, 
            "https://via.placeholder.com/300x400?text=Giay+C", "Shoes"));
        PRODUCTS.put(101L, new Product(101L, "Túi xách D", "Túi xách nữ thời trang", 850000.0,
            "https://via.placeholder.com/300x400?text=Tui+D", "Accessories"));
        PRODUCTS.put(102L, new Product(102L, "Mũ E", "Mũ lưỡi trai nam", 150000.0,
            "https://via.placeholder.com/300x400?text=Mu+E", "Accessories"));
    }

    @PostMapping("/room")
    public ResponseEntity<?> createRoom(
            @Valid @RequestBody CreateRoomRequest request) {

        liveKitService.createRoom(request.getRoomName());
        return ResponseEntity.ok(Map.of("message", "Room created"));
    }

    @PostMapping("/token")
    public ResponseEntity<?> createToken(
            @Valid @RequestBody TokenRequest request) {

        String token = liveKitService.generateToken(
                request.getRoomName(),
                request.getIdentity()
        );

        return ResponseEntity.ok(Map.of(
                "token", token,
                "url", "wss://livetream-7f2wlo6u.livekit.cloud"
        ));
    }

    // ==================== WHISPER INTEGRATION ====================

    /**
     * 🎤 Nhận audio từ frontend, gửi đến Whisper để transcribe
     * Sau đó trích xuất productId và broadcast cho phòng live
     */
    @PostMapping(value = "/speech-to-product", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> speechToProduct(
            @RequestParam("audio") MultipartFile audioFile,
            @RequestParam("roomName") String roomName) {

        try {
            // 1. Gửi audio đến Python Whisper service
            WhisperResponse whisperResult = whisperService.transcribeAudio(audioFile);

            // 2. Trích xuất productId
            String productIdStr = whisperResult.getProductId();
            
            if (productIdStr == null || productIdStr.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Không tìm thấy ID sản phẩm trong audio",
                    "transcript", whisperResult.getText()
                ));
            }

            Long productId = Long.parseLong(productIdStr);
            
            // 3. Lưu product cho room
            roomProducts.put(roomName, productId);
            
            // 4. Lấy thông tin product
            Product product = PRODUCTS.get(productId);
            
            if (product == null) {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Sản phẩm không tồn tại",
                    "productId", productId,
                    "transcript", whisperResult.getText()
                ));
            }

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đã nhận diện sản phẩm thành công",
                "transcript", whisperResult.getText(),
                "productId", productId,
                "product", product,
                "roomName", roomName
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    /**
     * 📦 Broadcast sản phẩm cho phòng live (gọi từ Streamer)
     */
    @PostMapping("/broadcast-product")
    public ResponseEntity<?> broadcastProduct(@RequestBody BroadcastProductRequest request) {
        String roomName = request.getRoomName();
        Long productId = request.getProductId();
        
        // Lưu product cho room
        roomProducts.put(roomName, productId);
        
        // Lấy product
        Product product = PRODUCTS.get(productId);
        
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "product", product,
            "roomName", roomName
        ));
    }

    /**
     * 📺 Lấy sản phẩm đang hiển thị cho phòng live (gọi từ Viewer)
     */
    @GetMapping("/room/{roomName}/current-product")
    public ResponseEntity<?> getCurrentProduct(@PathVariable String roomName) {
        Long productId = roomProducts.get(roomName);
        
        if (productId == null) {
            return ResponseEntity.ok(Map.of(
                "hasProduct", false,
                "message", "Chưa có sản phẩm nào được giới thiệu"
            ));
        }
        
        Product product = PRODUCTS.get(productId);
        
        if (product == null) {
            return ResponseEntity.ok(Map.of(
                "hasProduct", false,
                "productId", productId,
                "message", "Sản phẩm không tồn tại"
            ));
        }
        
        return ResponseEntity.ok(Map.of(
            "hasProduct", true,
            "productId", productId,
            "product", product
        ));
    }

    /**
     * 🗑️ Xóa sản phẩm khỏi phòng live
     */
    @DeleteMapping("/room/{roomName}/current-product")
    public ResponseEntity<?> clearCurrentProduct(@PathVariable String roomName) {
        roomProducts.remove(roomName);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Đã xóa sản phẩm khỏi phòng"
        ));
    }
}
