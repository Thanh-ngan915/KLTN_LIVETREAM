package org.example.livetreamservice.service;

import lombok.RequiredArgsConstructor;
import org.example.livetreamservice.dto.WhisperResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class WhisperService {

    @Value("${whisper.service.url:http://localhost:5000}")
    private String whisperServiceUrl;

    private final RestTemplate restTemplate;

    /**
     * Gửi audio file đến Whisper service để transcribe
     */
    public WhisperResponse transcribeAudio(MultipartFile audioFile) {
        try {
            String url = whisperServiceUrl + "/speech-to-text";

            // Prepare multipart request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // Create resource with proper content type
            final String filename = audioFile.getOriginalFilename() != null 
                ? audioFile.getOriginalFilename() 
                : "recording.webm";
            final String contentType = audioFile.getContentType() != null 
                ? audioFile.getContentType() 
                : "audio/webm";
            
            System.out.println("📤 Sending audio to Whisper: " + filename + ", type: " + contentType + ", size: " + audioFile.getSize());

            // Create HTTP entity with content type for the file
            HttpHeaders fileHeaders = new HttpHeaders();
            fileHeaders.setContentType(MediaType.parseMediaType(contentType));
            HttpEntity<ByteArrayResource> fileEntity = new HttpEntity<>(
                new ByteArrayResource(audioFile.getBytes()) {
                    @Override
                    public String getFilename() {
                        return filename;
                    }
                }, 
                fileHeaders
            );

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("audio", fileEntity);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = 
                new HttpEntity<>(body, headers);

            // Call Python Whisper service
            ResponseEntity<WhisperResponse> response = restTemplate.postForEntity(
                url, 
                requestEntity, 
                WhisperResponse.class
            );

            return response.getBody();

        } catch (Exception e) {
            throw new RuntimeException("Failed to transcribe audio: " + e.getMessage(), e);
        }
    }

    /**
     * Trích xuất product ID từ text (không cần audio)
     */
    public WhisperResponse extractProductIdFromText(String text) {
        try {
            String url = whisperServiceUrl + "/extract-product-id?text=" + text;
            
            ResponseEntity<WhisperResponse> response = restTemplate.postForEntity(
                url,
                null,
                WhisperResponse.class
            );

            return response.getBody();

        } catch (Exception e) {
            throw new RuntimeException("Failed to extract product ID: " + e.getMessage(), e);
        }
    }
}
