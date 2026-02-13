package org.example.livetreamservice.service;


import io.jsonwebtoken.Jwts;
import io.livekit.server.RoomServiceClient;
import lombok.RequiredArgsConstructor;
import org.example.livetreamservice.config.LiveKitConfig;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LiveKitService {

    private final LiveKitConfig config;

    public void createRoom(String roomName) {
        RoomServiceClient client = RoomServiceClient.create(
                config.getUrl(),
                config.getApiKey(),
                config.getApiSecret()
        );

        try {
            client.createRoom(roomName);
        } catch (Exception ignored) {
            // Room tồn tại thì bỏ qua
        }
    }

    public String generateToken(String roomName, String identity) {
        // Tạo video grant theo cấu trúc docs LiveKit
        Map<String, Object> videoGrant = new HashMap<>();
        videoGrant.put("room", roomName);
        videoGrant.put("roomJoin", true);
        videoGrant.put("canPublish", true);
        videoGrant.put("canSubscribe", true);
        
        // Tạo claims cho JWT token
        Map<String, Object> claims = new HashMap<>();
        claims.put("video", videoGrant);
        claims.put("sub", identity);  // subject = identity
        claims.put("name", identity);
        
        // Tạo secret key từ API secret
        SecretKey key = new SecretKeySpec(
            config.getApiSecret().getBytes(),
            "HmacSHA256"
        );
        
        // Tạo JWT token
        return Jwts.builder()
                .issuer(config.getApiKey())  // iss = API key
                .subject(identity)            // sub = identity
                .claims(claims)               // 
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000)) // 24h
                .signWith(key)
                .compact();
    }
}
