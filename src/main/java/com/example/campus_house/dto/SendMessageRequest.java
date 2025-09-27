package com.example.campus_house.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "메시지 전송 요청")
public class SendMessageRequest {
    @Schema(description = "메시지 내용", example = "안녕하세요!", required = true)
    private String content;
    
    @Schema(description = "이미지 URL", example = "https://example.com/image.jpg")
    private String imageUrl;
    
    @Schema(description = "메시지 타입", example = "TEXT", allowableValues = {"TEXT", "IMAGE", "FILE"}, required = true)
    private String messageType; // TEXT, IMAGE, FILE
}
