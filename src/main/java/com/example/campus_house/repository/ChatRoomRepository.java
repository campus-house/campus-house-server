package com.example.campus_house.repository;

import com.example.campus_house.entity.ChatRoom;
import com.example.campus_house.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    
    // 사용자가 참여한 모든 채팅방 조회 (최신순)
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.user1 = :user OR cr.user2 = :user ORDER BY cr.createdAt DESC")
    List<ChatRoom> findByUserOrderByCreatedAtDesc(@Param("user") User user);
    
    // 두 사용자 간의 채팅방 조회
    @Query("SELECT cr FROM ChatRoom cr WHERE " +
           "(cr.user1 = :user1 AND cr.user2 = :user2) OR " +
           "(cr.user1 = :user2 AND cr.user2 = :user1)")
    Optional<ChatRoom> findByUsers(@Param("user1") User user1, @Param("user2") User user2);
    
    // 사용자 이름으로 채팅방 조회
    @Query("SELECT cr FROM ChatRoom cr WHERE " +
           "(cr.user1.userName = :user1Name AND cr.user2.userName = :user2Name) OR " +
           "(cr.user1.userName = :user2Name AND cr.user2.userName = :user1Name)")
    Optional<ChatRoom> findByUserNames(@Param("user1Name") String user1Name, @Param("user2Name") String user2Name);
    
    // 특정 사용자가 참여한 채팅방 존재 여부 확인
    boolean existsByUser1OrUser2(User user1, User user2);
    
    // 사용자 이름으로 참여한 채팅방 목록 조회
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.user1.userName = :userName OR cr.user2.userName = :userName ORDER BY cr.createdAt DESC")
    List<ChatRoom> findByUserNameOrderByCreatedAtDesc(@Param("userName") String userName);
}
