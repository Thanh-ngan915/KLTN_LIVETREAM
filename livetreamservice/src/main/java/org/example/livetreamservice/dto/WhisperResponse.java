package org.example.livetreamservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WhisperResponse {
    private String text;
    private String productId;
    private List<String> numbers;
    private Double confidence;
}
