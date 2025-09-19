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
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    // 사용자별 읽지 않은 알림 조회
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);
    
    // 사용자별 읽지 않은 알림 개수
    Long countByUserIdAndIsReadFalse(Long userId);
    
    // 사용자별 특정 타입의 알림 조회
    List<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, Notification.NotificationType type);
    
    // 사용자별 기간별 알림 조회
    List<Notification> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    // 특정 관련 ID와 타입으로 알림 조회 (중복 방지용)
    List<Notification> findByUserIdAndRelatedIdAndRelatedTypeAndType(
            Long userId, String relatedId, String relatedType, Notification.NotificationType type);
    
    // 읽지 않은 알림을 읽음으로 표시
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
    void markAllAsReadByUserId(@Param("userId") Long userId);
    
    // 특정 알림을 읽음으로 표시
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :notificationId AND n.user.id = :userId")
    void markAsReadByIdAndUserId(@Param("notificationId") Long notificationId, @Param("userId") Long userId);
    
    // 오래된 알림 삭제 (30일 이상)
    @Query("DELETE FROM Notification n WHERE n.createdAt < :cutoffDate")
    void deleteOldNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);
}
