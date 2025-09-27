package com.example.campus_house.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "채팅", description = "채팅 관련 API (구현 예정)")
public class ChatController {
    
    // 채팅방 생성/조회
    @Operation(summary = "채팅방 생성/조회", description = "이미 채팅방이 있으면 기존 방 반환, 없으면 새 방 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅방 조회/생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @PostMapping("/rooms")
    public ResponseEntity<String> createOrGetChatRoom(
            @RequestHeader("Authorization") String token) {
        // TODO: 채팅 기능 구현 예정
        return ResponseEntity.ok("채팅 기능은 구현 예정입니다.");
    }
    
    // 사용자의 채팅방 목록
    @Operation(summary = "사용자 채팅방 목록", description = "특정 사용자의 채팅방 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅방 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @GetMapping("/rooms/user/{userName}")
    public ResponseEntity<String> getUserChatRooms(
            @Parameter(description = "사용자 이름", required = true)
            @PathVariable String userName,
            @RequestHeader("Authorization") String token) {
        // TODO: 채팅 기능 구현 예정
        return ResponseEntity.ok("채팅 기능은 구현 예정입니다.");
    }
    
    // 특정 채팅방 조회
    @Operation(summary = "채팅방 조회", description = "특정 채팅방의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅방 조회 성공"),
            @ApiResponse(responseCode = "404", description = "채팅방을 찾을 수 없음")
    })
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<String> getChatRoom(
            @Parameter(description = "채팅방 ID", required = true)
            @PathVariable Long roomId,
            @RequestHeader("Authorization") String token) {
        // TODO: 채팅 기능 구현 예정
        return ResponseEntity.ok("채팅 기능은 구현 예정입니다.");
    }
    
    // 두 사용자 간 채팅방 조회
    @Operation(summary = "두 사용자 간 채팅방 조회", description = "두 사용자 간의 채팅방을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅방 조회 성공"),
            @ApiResponse(responseCode = "404", description = "채팅방을 찾을 수 없음")
    })
    @GetMapping("/rooms/users/{user1Name}/{user2Name}")
    public ResponseEntity<String> getChatRoomByUsers(
            @Parameter(description = "사용자1 이름", required = true)
            @PathVariable String user1Name,
            @Parameter(description = "사용자2 이름", required = true)
            @PathVariable String user2Name,
            @RequestHeader("Authorization") String token) {
        // TODO: 채팅 기능 구현 예정
        return ResponseEntity.ok("채팅 기능은 구현 예정입니다.");
    }
    
    // 메시지 전송
    @Operation(summary = "메시지 전송", description = "채팅방에 메시지를 전송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "메시지 전송 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @PostMapping("/rooms/{roomId}/messages")
    public ResponseEntity<String> sendMessage(
            @Parameter(description = "채팅방 ID", required = true)
            @PathVariable Long roomId,
            @RequestHeader("Authorization") String token) {
        // TODO: 채팅 기능 구현 예정
        return ResponseEntity.ok("채팅 기능은 구현 예정입니다.");
    }
    
    // 메시지 목록 조회
    @Operation(summary = "메시지 목록 조회", description = "채팅방의 메시지 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메시지 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "채팅방을 찾을 수 없음")
    })
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<String> getMessages(
            @Parameter(description = "채팅방 ID", required = true)
            @PathVariable Long roomId,
            @RequestHeader("Authorization") String token) {
        // TODO: 채팅 기능 구현 예정
        return ResponseEntity.ok("채팅 기능은 구현 예정입니다.");
    }
    
    // 메시지 읽음 처리
    @Operation(summary = "메시지 읽음 처리", description = "채팅방의 메시지를 읽음 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "읽음 처리 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @PostMapping("/rooms/{roomId}/read")
    public ResponseEntity<String> markAsRead(
            @Parameter(description = "채팅방 ID", required = true)
            @PathVariable Long roomId,
            @RequestHeader("Authorization") String token) {
        // TODO: 채팅 기능 구현 예정
        return ResponseEntity.ok("채팅 기능은 구현 예정입니다.");
    }
    
    // 읽지 않은 메시지 수 조회
    @Operation(summary = "읽지 않은 메시지 수 조회", description = "사용자의 읽지 않은 메시지 수를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "읽지 않은 메시지 수 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @GetMapping("/unread-count/{userName}")
    public ResponseEntity<String> getUnreadCount(
            @Parameter(description = "사용자 이름", required = true)
            @PathVariable String userName,
            @RequestHeader("Authorization") String token) {
        // TODO: 채팅 기능 구현 예정
        return ResponseEntity.ok("채팅 기능은 구현 예정입니다.");
    }
    
    // 특정 채팅방의 읽지 않은 메시지 수 조회
    @Operation(summary = "특정 채팅방 읽지 않은 메시지 수", description = "특정 채팅방의 읽지 않은 메시지 수를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "읽지 않은 메시지 수 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @GetMapping("/rooms/{roomId}/unread-count/{userName}")
    public ResponseEntity<String> getUnreadCountByRoom(
            @Parameter(description = "채팅방 ID", required = true)
            @PathVariable Long roomId,
            @Parameter(description = "사용자 이름", required = true)
            @PathVariable String userName,
            @RequestHeader("Authorization") String token) {
        // TODO: 채팅 기능 구현 예정
        return ResponseEntity.ok("채팅 기능은 구현 예정입니다.");
    }
}
