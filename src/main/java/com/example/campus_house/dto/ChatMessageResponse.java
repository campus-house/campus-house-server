package com.example.campus_house.dto;

import com.example.campus_house.entity.ChatMessage;
import com.example.campus_house.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "채팅 메시지 응답 정보")
public class ChatMessageResponse {
    @Schema(description = "메시지 ID", example = "1")
    private Long id;
    
    @Schema(description = "메시지 내용", example = "안녕하세요!")
    private String content;
    
    @Schema(description = "이미지 URL", example = "https://example.com/image.jpg")
    private String imageUrl;
    
    @Schema(description = "메시지 타입", example = "TEXT", allowableValues = {"TEXT", "IMAGE", "FILE"})
    private String messageType;
    
    @Schema(description = "발신자 이름", example = "홍길동")
    private String senderName;
    
    @Schema(description = "발신자 닉네임", example = "홍길동")
    private String senderNickname;
    
    @Schema(description = "발신자 프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String senderProfileImage;
    
    @Schema(description = "메시지 생성 시간", example = "2024-01-15T14:25:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "현재 사용자가 보낸 메시지인지 여부", example = "true")
    private boolean isFromCurrentUser;
    
    public static ChatMessageResponse from(ChatMessage message, User currentUser) {
        return ChatMessageResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .imageUrl(message.getImageUrl())
                .messageType(message.getType().name())
                .senderName(message.getSender().getNickname())
                .senderNickname(message.getSender().getNickname())
                .senderProfileImage(message.getSender().getProfileImage())
                .createdAt(message.getCreatedAt())
                .isFromCurrentUser(message.getSender().equals(currentUser))
                .build();
    }
}
