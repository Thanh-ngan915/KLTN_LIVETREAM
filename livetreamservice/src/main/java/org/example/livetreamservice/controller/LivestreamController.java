package org.example.livetreamservice.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.livetreamservice.dto.CreateRoomRequest;
import org.example.livetreamservice.dto.TokenRequest;
import org.example.livetreamservice.service.LiveKitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/livestream")
@RequiredArgsConstructor
public class LivestreamController {

    private final LiveKitService liveKitService;

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
}
