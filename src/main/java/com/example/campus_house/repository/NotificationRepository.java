package com.example.campus_house.repository;

import com.example.campus_house.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // 사용자별 알림 조회 (페이징)
    @Query("SELECT n FROM Notification n WHERE n.user.userId = :userId ORDER BY n.createdAt DESC")
    Page<Notification> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);
    
    // 사용자별 읽지 않은 알림 조회
    @Query("SELECT n FROM Notification n WHERE n.user.userId = :userId AND n.isRead = false ORDER BY n.createdAt DESC")
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    // 사용자별 읽지 않은 알림 개수
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.userId = :userId AND n.isRead = false")
    Long countByUserIdAndIsReadFalse(@Param("userId") Long userId);
    
    // 사용자별 특정 타입의 알림 조회
    @Query("SELECT n FROM Notification n WHERE n.user.userId = :userId AND n.type = :type ORDER BY n.createdAt DESC")
    List<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(@Param("userId") Long userId, @Param("type") Notification.NotificationType type);
    
    // 사용자별 기간별 알림 조회
    @Query("SELECT n FROM Notification n WHERE n.user.userId = :userId AND n.createdAt BETWEEN :startDate AND :endDate ORDER BY n.createdAt DESC")
    List<Notification> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            @Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // 특정 관련 ID와 타입으로 알림 조회 (중복 방지용)
    @Query("SELECT n FROM Notification n WHERE n.user.userId = :userId AND n.relatedId = :relatedId AND n.relatedType = :relatedType AND n.type = :type")
    List<Notification> findByUserIdAndRelatedIdAndRelatedTypeAndType(
            @Param("userId") Long userId, @Param("relatedId") String relatedId, @Param("relatedType") String relatedType, @Param("type") Notification.NotificationType type);
    
    // 읽지 않은 알림을 읽음으로 표시
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.userId = :userId AND n.isRead = false")
    void markAllAsReadByUserId(@Param("userId") Long userId);
    
    // 특정 알림을 읽음으로 표시
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :notificationId AND n.user.userId = :userId")
    void markAsReadByIdAndUserId(@Param("notificationId") Long notificationId, @Param("userId") Long userId);
    
    // 오래된 알림 삭제 (30일 이상)
    @Query("DELETE FROM Notification n WHERE n.createdAt < :cutoffDate")
    void deleteOldNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);
}
