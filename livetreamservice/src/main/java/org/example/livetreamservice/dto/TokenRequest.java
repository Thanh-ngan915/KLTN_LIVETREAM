package org.example.livetreamservice.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenRequest {
    @NotBlank
    private String roomName;

    @NotBlank
    private String identity;
}
