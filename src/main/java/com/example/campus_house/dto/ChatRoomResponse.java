package com.example.campus_house.dto;

import com.example.campus_house.entity.ChatRoom;
import com.example.campus_house.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "채팅방 응답 정보")
public class ChatRoomResponse {
    @Schema(description = "채팅방 ID", example = "1")
    private Long id;
    
    @Schema(description = "상대방 사용자 이름", example = "홍길동")
    private String otherUserName;
    
    @Schema(description = "상대방 사용자 닉네임", example = "홍길동")
    private String otherUserNickname;
    
    @Schema(description = "상대방 프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String otherUserProfileImage;
    
    @Schema(description = "채팅방 생성 시간", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "마지막 메시지 시간", example = "2024-01-15T14:25:00")
    private LocalDateTime lastMessageTime;
    
    @Schema(description = "마지막 메시지 내용", example = "안녕하세요!")
    private String lastMessageContent;
    
    @Schema(description = "읽지 않은 메시지 수", example = "3")
    private Long unreadCount;
    
    public static ChatRoomResponse from(ChatRoom chatRoom, User currentUser, String lastMessageContent, 
                                      LocalDateTime lastMessageTime, Long unreadCount) {
        User otherUser = chatRoom.getUser1().equals(currentUser) ? chatRoom.getUser2() : chatRoom.getUser1();
        
        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .otherUserName(otherUser.getNickname())
                .otherUserNickname(otherUser.getNickname())
                .otherUserProfileImage(otherUser.getProfileImage())
                .createdAt(chatRoom.getCreatedAt())
                .lastMessageTime(lastMessageTime)
                .lastMessageContent(lastMessageContent)
                .unreadCount(unreadCount)
                .build();
    }
}
