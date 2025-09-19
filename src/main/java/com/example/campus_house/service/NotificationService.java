package com.example.campus_house.service;

import com.example.campus_house.entity.Notification;
import com.example.campus_house.entity.User;
import com.example.campus_house.repository.NotificationRepository;
import com.example.campus_house.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    
    // 알림 생성
    @Transactional
    public Notification createNotification(Long userId, Notification.NotificationType type, 
                                         String title, String content, String relatedId, String relatedType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .content(content)
                .relatedId(relatedId)
                .relatedType(relatedType)
                .isRead(false)
                .build();
        
        return notificationRepository.save(notification);
    }
    
    // 다른 사용자로부터 알림 생성
    @Transactional
    public Notification createNotificationFromUser(Long userId, Long fromUserId, Notification.NotificationType type,
                                                  String title, String content, String relatedId, String relatedType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        User fromUser = fromUserId != null ? userRepository.findById(fromUserId)
                .orElseThrow(() -> new RuntimeException("알림을 보낸 사용자를 찾을 수 없습니다.")) : null;
        
        // 자기 자신에게는 알림을 보내지 않음
        if (fromUserId != null && userId.equals(fromUserId)) {
            return null;
        }
        
        Notification notification = Notification.builder()
                .user(user)
                .fromUser(fromUser)
                .type(type)
                .title(title)
                .content(content)
                .relatedId(relatedId)
                .relatedType(relatedType)
                .isRead(false)
                .build();
        
        return notificationRepository.save(notification);
    }
    
    // 중복 알림 방지 (같은 관련 ID와 타입으로 이미 알림이 있는지 확인)
    @Transactional
    public Notification createNotificationIfNotExists(Long userId, Long fromUserId, Notification.NotificationType type,
                                                     String title, String content, String relatedId, String relatedType) {
        // 이미 같은 알림이 있는지 확인
        List<Notification> existingNotifications = notificationRepository.findByUserIdAndRelatedIdAndRelatedTypeAndType(
                userId, relatedId, relatedType, type);
        
        if (!existingNotifications.isEmpty()) {
            return null; // 이미 존재하는 알림
        }
        
        return createNotificationFromUser(userId, fromUserId, type, title, content, relatedId, relatedType);
    }
    
    // 사용자별 알림 조회 (페이징)
    public Page<Notification> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
    
    // 사용자별 읽지 않은 알림 조회
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }
    
    // 사용자별 읽지 않은 알림 개수
    public Long getUnreadNotificationCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }
    
    // 알림 읽음 처리
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        notificationRepository.markAsReadByIdAndUserId(notificationId, userId);
    }
    
    // 모든 알림 읽음 처리
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }
    
    // 알림 삭제
    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없습니다."));
        
        if (!notification.getUser().getId().equals(userId)) {
            throw new RuntimeException("본인의 알림만 삭제할 수 있습니다.");
        }
        
        notificationRepository.delete(notification);
    }
    
    // 오래된 알림 정리 (30일 이상)
    @Transactional
    public void cleanupOldNotifications() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        notificationRepository.deleteOldNotifications(cutoffDate);
    }
    
    // 특정 타입의 알림 조회
    public List<Notification> getNotificationsByType(Long userId, Notification.NotificationType type) {
        return notificationRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, type);
    }
    
    // 기간별 알림 조회
    public List<Notification> getNotificationsByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return notificationRepository.findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(userId, startDate, endDate);
    }
}
