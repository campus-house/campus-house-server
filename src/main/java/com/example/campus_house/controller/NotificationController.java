package com.example.campus_house.controller;

import com.example.campus_house.entity.Notification;
import com.example.campus_house.service.AuthService;
import com.example.campus_house.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    private final AuthService authService;
    
    // 내 알림 목록 조회 (페이징)
    @GetMapping
    public ResponseEntity<Page<Notification>> getMyNotifications(
            @RequestHeader("Authorization") String token,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        try {
            String jwtToken = token.substring(7);
            Long userId = authService.getUserFromToken(jwtToken).getId();
            Page<Notification> notifications = notificationService.getUserNotifications(userId, pageable);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 읽지 않은 알림 조회
    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(
            @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            Long userId = authService.getUserFromToken(jwtToken).getId();
            List<Notification> notifications = notificationService.getUnreadNotifications(userId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 읽지 않은 알림 개수 조회
    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadNotificationCount(
            @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            Long userId = authService.getUserFromToken(jwtToken).getId();
            Long count = notificationService.getUnreadNotificationCount(userId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 알림 읽음 처리
    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @RequestHeader("Authorization") String token,
            @PathVariable Long notificationId) {
        try {
            String jwtToken = token.substring(7);
            Long userId = authService.getUserFromToken(jwtToken).getId();
            notificationService.markAsRead(notificationId, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 모든 알림 읽음 처리
    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            Long userId = authService.getUserFromToken(jwtToken).getId();
            notificationService.markAllAsRead(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 알림 삭제
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @RequestHeader("Authorization") String token,
            @PathVariable Long notificationId) {
        try {
            String jwtToken = token.substring(7);
            Long userId = authService.getUserFromToken(jwtToken).getId();
            notificationService.deleteNotification(notificationId, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 특정 타입의 알림 조회
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Notification>> getNotificationsByType(
            @RequestHeader("Authorization") String token,
            @PathVariable String type) {
        try {
            String jwtToken = token.substring(7);
            Long userId = authService.getUserFromToken(jwtToken).getId();
            Notification.NotificationType notificationType = Notification.NotificationType.valueOf(type.toUpperCase());
            List<Notification> notifications = notificationService.getNotificationsByType(userId, notificationType);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
