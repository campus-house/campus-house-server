package com.example.campus_house.controller;

import com.example.campus_house.dto.ChatMessageResponse;
import com.example.campus_house.dto.ChatRoomResponse;
import com.example.campus_house.dto.SendMessageRequest;
import com.example.campus_house.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "채팅", description = "채팅 관련 API")
public class ChatController {
    
    private final ChatService chatService;
    
    // 채팅방 생성/조회
    @Operation(
        summary = "채팅방 생성/조회", 
        description = "이미 채팅방이 있으면 기존 방 반환, 없으면 새 방 생성",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200", 
                description = "채팅방 조회/생성 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ChatRoomResponse.class),
                    examples = @ExampleObject(
                        name = "성공 응답",
                        value = """
                        {
                          "id": 1,
                          "otherUserName": "홍길동",
                          "otherUserNickname": "홍길동",
                          "otherUserProfileImage": "https://example.com/profile.jpg",
                          "createdAt": "2024-01-15T10:30:00",
                          "lastMessageTime": "2024-01-15T14:25:00",
                          "lastMessageContent": "안녕하세요!",
                          "unreadCount": 3
                        }
                        """
                    )
                )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @PostMapping("/rooms")
    public ResponseEntity<?> createOrGetChatRoom(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "상대방 사용자 닉네임", required = true, example = "홍길동")
            @RequestParam String otherUserName) {
        try {
            ChatRoomResponse response = chatService.createOrGetChatRoom(token, otherUserName);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // 사용자의 채팅방 목록
    @Operation(
        summary = "사용자 채팅방 목록", 
        description = "특정 사용자의 채팅방 목록을 조회합니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200", 
                description = "채팅방 목록 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ChatRoomResponse[].class)
                )
            ),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "다른 사용자의 채팅방 목록 조회 권한 없음")
    })
    @GetMapping("/rooms/user/{userName}")
    public ResponseEntity<?> getUserChatRooms(
            @Parameter(description = "사용자 닉네임", required = true, example = "홍길동")
            @PathVariable String userName,
            @RequestHeader("Authorization") String token) {
        try {
            List<ChatRoomResponse> response = chatService.getUserChatRooms(token, userName);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // 특정 채팅방 조회
    @Operation(
        summary = "채팅방 조회", 
        description = "특정 채팅방의 정보를 조회합니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200", 
                description = "채팅방 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ChatRoomResponse.class)
                )
            ),
            @ApiResponse(responseCode = "404", description = "채팅방을 찾을 수 없음"),
            @ApiResponse(responseCode = "403", description = "채팅방 접근 권한 없음")
    })
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<?> getChatRoom(
            @Parameter(description = "채팅방 ID", required = true, example = "1")
            @PathVariable Long roomId,
            @RequestHeader("Authorization") String token) {
        try {
            ChatRoomResponse response = chatService.getChatRoom(token, roomId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // 두 사용자 간 채팅방 조회
    @Operation(
        summary = "두 사용자 간 채팅방 조회", 
        description = "두 사용자 간의 채팅방을 조회합니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200", 
                description = "채팅방 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ChatRoomResponse.class)
                )
            ),
            @ApiResponse(responseCode = "404", description = "채팅방을 찾을 수 없음"),
            @ApiResponse(responseCode = "403", description = "채팅방 접근 권한 없음")
    })
    @GetMapping("/rooms/users/{user1Name}/{user2Name}")
    public ResponseEntity<?> getChatRoomByUsers(
            @Parameter(description = "사용자1 닉네임", required = true, example = "홍길동")
            @PathVariable String user1Name,
            @Parameter(description = "사용자2 닉네임", required = true, example = "김철수")
            @PathVariable String user2Name,
            @RequestHeader("Authorization") String token) {
        try {
            ChatRoomResponse response = chatService.getChatRoomByUsers(token, user1Name, user2Name);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // 메시지 전송
    @Operation(
        summary = "메시지 전송", 
        description = "채팅방에 메시지를 전송합니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200", 
                description = "메시지 전송 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ChatMessageResponse.class),
                    examples = @ExampleObject(
                        name = "성공 응답",
                        value = """
                        {
                          "id": 1,
                          "content": "안녕하세요!",
                          "imageUrl": null,
                          "messageType": "TEXT",
                          "senderName": "홍길동",
                          "senderNickname": "홍길동",
                          "senderProfileImage": "https://example.com/profile.jpg",
                          "createdAt": "2024-01-15T14:25:00",
                          "isFromCurrentUser": true
                        }
                        """
                    )
                )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "채팅방 접근 권한 없음")
    })
    @PostMapping("/rooms/{roomId}/messages")
    public ResponseEntity<?> sendMessage(
            @Parameter(description = "채팅방 ID", required = true, example = "1")
            @PathVariable Long roomId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "메시지 전송 요청",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SendMessageRequest.class),
                    examples = @ExampleObject(
                        name = "텍스트 메시지",
                        value = """
                        {
                          "content": "안녕하세요!",
                          "messageType": "TEXT"
                        }
                        """
                    )
                )
            )
            @RequestBody SendMessageRequest request,
            @RequestHeader("Authorization") String token) {
        try {
            ChatMessageResponse response = chatService.sendMessage(token, roomId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // 메시지 목록 조회
    @Operation(
        summary = "메시지 목록 조회", 
        description = "채팅방의 메시지 목록을 조회합니다. (페이징 지원)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200", 
                description = "메시지 목록 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ChatMessageResponse[].class)
                )
            ),
            @ApiResponse(responseCode = "404", description = "채팅방을 찾을 수 없음"),
            @ApiResponse(responseCode = "403", description = "채팅방 접근 권한 없음")
    })
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<?> getMessages(
            @Parameter(description = "채팅방 ID", required = true, example = "1")
            @PathVariable Long roomId,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지당 메시지 수", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader("Authorization") String token) {
        try {
            List<ChatMessageResponse> response = chatService.getMessages(token, roomId, page, size);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // 메시지 읽음 처리
    @Operation(
        summary = "메시지 읽음 처리", 
        description = "채팅방의 메시지를 읽음 처리합니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200", 
                description = "읽음 처리 성공",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        name = "성공 응답",
                        value = """
                        {
                          "message": "읽음 처리 완료"
                        }
                        """
                    )
                )
            ),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "채팅방 접근 권한 없음")
    })
    @PostMapping("/rooms/{roomId}/read")
    public ResponseEntity<?> markAsRead(
            @Parameter(description = "채팅방 ID", required = true, example = "1")
            @PathVariable Long roomId,
            @RequestHeader("Authorization") String token) {
        try {
            chatService.markAsRead(token, roomId);
            return ResponseEntity.ok(Map.of("message", "읽음 처리 완료"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // 읽지 않은 메시지 수 조회
    @Operation(
        summary = "읽지 않은 메시지 수 조회", 
        description = "사용자의 읽지 않은 메시지 수를 조회합니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200", 
                description = "읽지 않은 메시지 수 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        name = "성공 응답",
                        value = """
                        {
                          "unreadCount": 5
                        }
                        """
                    )
                )
            ),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "다른 사용자의 읽지 않은 메시지 수 조회 권한 없음")
    })
    @GetMapping("/unread-count/{userName}")
    public ResponseEntity<?> getUnreadCount(
            @Parameter(description = "사용자 닉네임", required = true, example = "홍길동")
            @PathVariable String userName,
            @RequestHeader("Authorization") String token) {
        try {
            Long unreadCount = chatService.getUnreadCount(token, userName);
            return ResponseEntity.ok(Map.of("unreadCount", unreadCount));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // 특정 채팅방의 읽지 않은 메시지 수 조회
    @Operation(
        summary = "특정 채팅방 읽지 않은 메시지 수", 
        description = "특정 채팅방의 읽지 않은 메시지 수를 조회합니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200", 
                description = "읽지 않은 메시지 수 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        name = "성공 응답",
                        value = """
                        {
                          "unreadCount": 2
                        }
                        """
                    )
                )
            ),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "채팅방 접근 권한 없음")
    })
    @GetMapping("/rooms/{roomId}/unread-count/{userName}")
    public ResponseEntity<?> getUnreadCountByRoom(
            @Parameter(description = "채팅방 ID", required = true, example = "1")
            @PathVariable Long roomId,
            @Parameter(description = "사용자 닉네임", required = true, example = "홍길동")
            @PathVariable String userName,
            @RequestHeader("Authorization") String token) {
        try {
            Long unreadCount = chatService.getUnreadCountByRoom(token, roomId, userName);
            return ResponseEntity.ok(Map.of("unreadCount", unreadCount));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
