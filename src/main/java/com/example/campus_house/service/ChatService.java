package com.example.campus_house.service;

import com.example.campus_house.dto.ChatMessageResponse;
import com.example.campus_house.dto.ChatRoomResponse;
import com.example.campus_house.dto.SendMessageRequest;
import com.example.campus_house.entity.ChatMessage;
import com.example.campus_house.entity.ChatRoom;
import com.example.campus_house.entity.User;
import com.example.campus_house.repository.ChatMessageRepository;
import com.example.campus_house.repository.ChatRoomRepository;
import com.example.campus_house.repository.UserRepository;
import com.example.campus_house.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {
    
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    
    // JWT 토큰에서 사용자 정보 추출
    private User getUserFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }
    
    // 채팅방 생성 또는 조회
    @Transactional
    public ChatRoomResponse createOrGetChatRoom(String token, String otherUserName) {
        User currentUser = getUserFromToken(token);
        User otherUser = userRepository.findByNickname(otherUserName)
                .orElseThrow(() -> new RuntimeException("상대방 사용자를 찾을 수 없습니다."));
        
        if (currentUser.equals(otherUser)) {
            throw new RuntimeException("자기 자신과는 채팅할 수 없습니다.");
        }
        
        // 기존 채팅방 조회
        Optional<ChatRoom> existingRoom = chatRoomRepository.findByUsers(currentUser, otherUser);
        
        ChatRoom chatRoom;
        if (existingRoom.isPresent()) {
            chatRoom = existingRoom.get();
        } else {
            // 새 채팅방 생성
            chatRoom = ChatRoom.builder()
                    .user1(currentUser)
                    .user2(otherUser)
                    .build();
            chatRoom = chatRoomRepository.save(chatRoom);
        }
        
        // 마지막 메시지 정보 조회
        String lastMessageContent = null;
        LocalDateTime lastMessageTime = null;
        Pageable pageable = PageRequest.of(0, 1);
        List<ChatMessage> lastMessages = chatMessageRepository.findLastMessageByChatRoom(chatRoom, pageable);
        if (!lastMessages.isEmpty()) {
            ChatMessage lastMessage = lastMessages.get(0);
            lastMessageContent = lastMessage.getContent();
            lastMessageTime = lastMessage.getCreatedAt();
        }
        
        // 읽지 않은 메시지 수 조회
        Long unreadCount = chatMessageRepository.countUnreadMessagesByChatRoomAndUser(chatRoom, currentUser);
        
        return ChatRoomResponse.from(chatRoom, currentUser, lastMessageContent, lastMessageTime, unreadCount);
    }
    
    // 사용자의 채팅방 목록 조회
    public List<ChatRoomResponse> getUserChatRooms(String token, String userName) {
        User currentUser = getUserFromToken(token);
        User targetUser = userRepository.findByNickname(userName)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 본인의 채팅방 목록만 조회 가능 (보안)
        if (!currentUser.equals(targetUser)) {
            throw new RuntimeException("다른 사용자의 채팅방 목록을 조회할 수 없습니다.");
        }
        
        List<ChatRoom> chatRooms = chatRoomRepository.findByUserOrderByCreatedAtDesc(targetUser);
        
        return chatRooms.stream().map(chatRoom -> {
            // 마지막 메시지 정보 조회
            String lastMessageContent = null;
            LocalDateTime lastMessageTime = null;
            Pageable pageable = PageRequest.of(0, 1);
            List<ChatMessage> lastMessages = chatMessageRepository.findLastMessageByChatRoom(chatRoom, pageable);
            if (!lastMessages.isEmpty()) {
                ChatMessage lastMessage = lastMessages.get(0);
                lastMessageContent = lastMessage.getContent();
                lastMessageTime = lastMessage.getCreatedAt();
            }
            
            // 읽지 않은 메시지 수 조회
            Long unreadCount = chatMessageRepository.countUnreadMessagesByChatRoomAndUser(chatRoom, targetUser);
            
            return ChatRoomResponse.from(chatRoom, targetUser, lastMessageContent, lastMessageTime, unreadCount);
        }).collect(Collectors.toList());
    }
    
    // 특정 채팅방 조회
    public ChatRoomResponse getChatRoom(String token, Long roomId) {
        User currentUser = getUserFromToken(token);
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
        
        // 채팅방 참여자 확인
        if (!chatRoom.getUser1().equals(currentUser) && !chatRoom.getUser2().equals(currentUser)) {
            throw new RuntimeException("채팅방에 접근할 권한이 없습니다.");
        }
        
        // 마지막 메시지 정보 조회
        String lastMessageContent = null;
        LocalDateTime lastMessageTime = null;
        Pageable pageable = PageRequest.of(0, 1);
        List<ChatMessage> lastMessages = chatMessageRepository.findLastMessageByChatRoom(chatRoom, pageable);
        if (!lastMessages.isEmpty()) {
            ChatMessage lastMessage = lastMessages.get(0);
            lastMessageContent = lastMessage.getContent();
            lastMessageTime = lastMessage.getCreatedAt();
        }
        
        // 읽지 않은 메시지 수 조회
        Long unreadCount = chatMessageRepository.countUnreadMessagesByChatRoomAndUser(chatRoom, currentUser);
        
        return ChatRoomResponse.from(chatRoom, currentUser, lastMessageContent, lastMessageTime, unreadCount);
    }
    
    // 두 사용자 간 채팅방 조회
    public ChatRoomResponse getChatRoomByUsers(String token, String user1Name, String user2Name) {
        User currentUser = getUserFromToken(token);
        
        // 현재 사용자가 두 사용자 중 하나인지 확인
        if (!currentUser.getNickname().equals(user1Name) && !currentUser.getNickname().equals(user2Name)) {
            throw new RuntimeException("채팅방에 접근할 권한이 없습니다.");
        }
        
        ChatRoom chatRoom = chatRoomRepository.findByUserNames(user1Name, user2Name)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
        
        // 마지막 메시지 정보 조회
        String lastMessageContent = null;
        LocalDateTime lastMessageTime = null;
        Pageable pageable = PageRequest.of(0, 1);
        List<ChatMessage> lastMessages = chatMessageRepository.findLastMessageByChatRoom(chatRoom, pageable);
        if (!lastMessages.isEmpty()) {
            ChatMessage lastMessage = lastMessages.get(0);
            lastMessageContent = lastMessage.getContent();
            lastMessageTime = lastMessage.getCreatedAt();
        }
        
        // 읽지 않은 메시지 수 조회
        Long unreadCount = chatMessageRepository.countUnreadMessagesByChatRoomAndUser(chatRoom, currentUser);
        
        return ChatRoomResponse.from(chatRoom, currentUser, lastMessageContent, lastMessageTime, unreadCount);
    }
    
    // 메시지 전송
    @Transactional
    public ChatMessageResponse sendMessage(String token, Long roomId, SendMessageRequest request) {
        User currentUser = getUserFromToken(token);
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
        
        // 채팅방 참여자 확인
        if (!chatRoom.getUser1().equals(currentUser) && !chatRoom.getUser2().equals(currentUser)) {
            throw new RuntimeException("채팅방에 메시지를 보낼 권한이 없습니다.");
        }
        
        // 메시지 타입 검증
        ChatMessage.MessageType messageType;
        try {
            messageType = ChatMessage.MessageType.valueOf(request.getMessageType().toUpperCase());
        } catch (IllegalArgumentException e) {
            messageType = ChatMessage.MessageType.TEXT;
        }
        
        // 메시지 생성 및 저장
        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(currentUser)
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .type(messageType)
                .build();
        
        message = chatMessageRepository.save(message);
        
        return ChatMessageResponse.from(message, currentUser);
    }
    
    // 메시지 목록 조회
    public List<ChatMessageResponse> getMessages(String token, Long roomId, int page, int size) {
        User currentUser = getUserFromToken(token);
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
        
        // 채팅방 참여자 확인
        if (!chatRoom.getUser1().equals(currentUser) && !chatRoom.getUser2().equals(currentUser)) {
            throw new RuntimeException("채팅방에 접근할 권한이 없습니다.");
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ChatMessage> messagePage = chatMessageRepository.findByChatRoomOrderByCreatedAtDesc(chatRoom, pageable);
        
        return messagePage.getContent().stream()
                .map(message -> ChatMessageResponse.from(message, currentUser))
                .collect(Collectors.toList());
    }
    
    // 메시지 읽음 처리
    @Transactional
    public void markAsRead(String token, Long roomId) {
        User currentUser = getUserFromToken(token);
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
        
        // 채팅방 참여자 확인
        if (!chatRoom.getUser1().equals(currentUser) && !chatRoom.getUser2().equals(currentUser)) {
            throw new RuntimeException("채팅방에 접근할 권한이 없습니다.");
        }
        
        // 마지막 읽음 시간 업데이트
        chatRoom.setLastReadTime(currentUser, LocalDateTime.now());
        chatRoomRepository.save(chatRoom);
    }
    
    // 읽지 않은 메시지 수 조회
    public Long getUnreadCount(String token, String userName) {
        User currentUser = getUserFromToken(token);
        
        // 본인의 읽지 않은 메시지 수만 조회 가능
        if (!currentUser.getNickname().equals(userName)) {
            throw new RuntimeException("다른 사용자의 읽지 않은 메시지 수를 조회할 수 없습니다.");
        }
        
        return chatMessageRepository.countUnreadMessagesByUser(currentUser);
    }
    
    // 특정 채팅방의 읽지 않은 메시지 수 조회
    public Long getUnreadCountByRoom(String token, Long roomId, String userName) {
        User currentUser = getUserFromToken(token);
        
        // 본인의 읽지 않은 메시지 수만 조회 가능
        if (!currentUser.getNickname().equals(userName)) {
            throw new RuntimeException("다른 사용자의 읽지 않은 메시지 수를 조회할 수 없습니다.");
        }
        
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
        
        // 채팅방 참여자 확인
        if (!chatRoom.getUser1().equals(currentUser) && !chatRoom.getUser2().equals(currentUser)) {
            throw new RuntimeException("채팅방에 접근할 권한이 없습니다.");
        }
        
        return chatMessageRepository.countUnreadMessagesByChatRoomAndUser(chatRoom, currentUser);
    }
}
