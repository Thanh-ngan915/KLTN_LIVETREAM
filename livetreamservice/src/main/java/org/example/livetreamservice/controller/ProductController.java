package org.example.livetreamservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.livetreamservice.dto.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    // Mock database - trong thực tế sẽ dùng Repository
    private static final Map<Long, Product> PRODUCTS = new HashMap<>();

    static {
        // Sample products
        PRODUCTS.put(123L, new Product(123L, "Quần A", "Quần jean nam cao cấp", 450000.0, 
            "https://via.placeholder.com/300x400?text=Quan+A", "Fashion"));
        PRODUCTS.put(456L, new Product(456L, "Áo B", "Áo thun nam trơn", 250000.0, 
            "https://via.placeholder.com/300x400?text=Ao+B", "Fashion"));
        PRODUCTS.put(789L, new Product(789L, "Giày C", "Giày thể thao Nike", 1200000.0, 
            "https://via.placeholder.com/300x400?text=Giay+C", "Shoes"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        Product product = PRODUCTS.get(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        return ResponseEntity.ok(PRODUCTS.values());
    }

    @PostMapping("/extract-from-text")
    public ResponseEntity<?> extractProductFromText(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        if (text == null || text.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Text is required"));
        }

        // Trích xuất ID từ text
        Long productId = extractProductId(text);
        if (productId == null) {
            return ResponseEntity.ok(Map.of("message", "No product ID found in text"));
        }

        // Lấy sản phẩm
        Product product = PRODUCTS.get(productId);
        if (product == null) {
            return ResponseEntity.ok(Map.of(
                "productId", productId,
                "message", "Product not found"
            ));
        }

        return ResponseEntity.ok(Map.of(
            "productId", productId,
            "product", product,
            "extractedFrom", text
        ));
    }

    /**
     * Trích xuất product ID từ text bằng regex
     * Ví dụ: "quần A có id là :123" -> 123
     */
    private Long extractProductId(String text) {
        // Pattern tìm số sau "id", "mã", hoặc số đứng độc lập
        Pattern pattern = Pattern.compile("(?:id|mã|số)[\\s:]*([0-9]+)|\\b([0-9]{3,})\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        
        if (matcher.find()) {
            String idStr = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
            try {
                return Long.parseLong(idStr);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
