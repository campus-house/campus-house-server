package com.example.campus_house.repository;

import com.example.campus_house.entity.ChatMessage;
import com.example.campus_house.entity.ChatRoom;
import com.example.campus_house.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    // 특정 채팅방의 메시지 목록 조회 (최신순, 페이징)
    Page<ChatMessage> findByChatRoomOrderByCreatedAtDesc(ChatRoom chatRoom, Pageable pageable);
    
    // 특정 채팅방의 메시지 목록 조회 (시간순)
    List<ChatMessage> findByChatRoomOrderByCreatedAtAsc(ChatRoom chatRoom);
    
    // 특정 채팅방에서 특정 시간 이후의 메시지 조회
    List<ChatMessage> findByChatRoomAndCreatedAtAfterOrderByCreatedAtAsc(ChatRoom chatRoom, LocalDateTime after);
    
    // 특정 채팅방에서 특정 시간 이후의 메시지 수 조회
    Long countByChatRoomAndCreatedAtAfter(ChatRoom chatRoom, LocalDateTime after);
    
    // 특정 사용자의 모든 읽지 않은 메시지 수 조회 (마지막 읽음 시간 기반)
    @Query("SELECT COUNT(cm) FROM ChatMessage cm " +
           "JOIN cm.chatRoom cr " +
           "WHERE (cr.user1 = :user OR cr.user2 = :user) " +
           "AND cm.sender != :user " +
           "AND ((cr.user1 = :user AND (cr.user1LastReadAt IS NULL OR cm.createdAt > cr.user1LastReadAt)) " +
           "OR (cr.user2 = :user AND (cr.user2LastReadAt IS NULL OR cm.createdAt > cr.user2LastReadAt)))")
    Long countUnreadMessagesByUser(@Param("user") User user);
    
    // 특정 사용자 이름의 모든 읽지 않은 메시지 수 조회 (마지막 읽음 시간 기반)
    @Query("SELECT COUNT(cm) FROM ChatMessage cm " +
           "JOIN cm.chatRoom cr " +
           "WHERE (cr.user1.userName = :userName OR cr.user2.userName = :userName) " +
           "AND cm.sender.userName != :userName " +
           "AND ((cr.user1.userName = :userName AND (cr.user1LastReadAt IS NULL OR cm.createdAt > cr.user1LastReadAt)) " +
           "OR (cr.user2.userName = :userName AND (cr.user2LastReadAt IS NULL OR cm.createdAt > cr.user2LastReadAt)))")
    Long countUnreadMessagesByUserName(@Param("userName") String userName);
    
    // 특정 채팅방에서 특정 사용자의 읽지 않은 메시지 수 조회 (마지막 읽음 시간 기반)
    @Query("SELECT COUNT(cm) FROM ChatMessage cm " +
           "WHERE cm.chatRoom = :chatRoom " +
           "AND cm.sender != :user " +
           "AND ((:chatRoom.user1 = :user AND (:chatRoom.user1LastReadAt IS NULL OR cm.createdAt > :chatRoom.user1LastReadAt)) " +
           "OR (:chatRoom.user2 = :user AND (:chatRoom.user2LastReadAt IS NULL OR cm.createdAt > :chatRoom.user2LastReadAt)))")
    Long countUnreadMessagesByChatRoomAndUser(@Param("chatRoom") ChatRoom chatRoom, @Param("user") User user);
    
    // 특정 채팅방에서 특정 사용자 이름의 읽지 않은 메시지 수 조회 (마지막 읽음 시간 기반)
    @Query("SELECT COUNT(cm) FROM ChatMessage cm " +
           "WHERE cm.chatRoom = :chatRoom " +
           "AND cm.sender.userName != :userName " +
           "AND ((:chatRoom.user1.userName = :userName AND (:chatRoom.user1LastReadAt IS NULL OR cm.createdAt > :chatRoom.user1LastReadAt)) " +
           "OR (:chatRoom.user2.userName = :userName AND (:chatRoom.user2LastReadAt IS NULL OR cm.createdAt > :chatRoom.user2LastReadAt)))")
    Long countUnreadMessagesByChatRoomAndUserName(@Param("chatRoom") ChatRoom chatRoom, @Param("userName") String userName);
    
    // 특정 채팅방의 마지막 메시지 조회
    @Query("SELECT cm FROM ChatMessage cm " +
           "WHERE cm.chatRoom = :chatRoom " +
           "ORDER BY cm.createdAt DESC")
    List<ChatMessage> findLastMessageByChatRoom(@Param("chatRoom") ChatRoom chatRoom, Pageable pageable);
}
