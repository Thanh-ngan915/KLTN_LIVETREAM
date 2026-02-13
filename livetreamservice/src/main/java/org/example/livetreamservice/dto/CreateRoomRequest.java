package org.example.livetreamservice.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateRoomRequest {
    @NotBlank
    private String roomName;
}
