package com.example.campus_house.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_rooms",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user1_id", "user2_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id")
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id")
    private User user2;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "user1_last_read_at")
    private LocalDateTime user1LastReadAt;
    
    @Column(name = "user2_last_read_at")
    private LocalDateTime user2LastReadAt;
    
    // 사용자별 마지막 읽음 시간 설정
    public void setLastReadTime(User user, LocalDateTime readTime) {
        if (user.equals(user1)) {
            this.user1LastReadAt = readTime;
        } else if (user.equals(user2)) {
            this.user2LastReadAt = readTime;
        }
    }
    
    // 사용자별 마지막 읽음 시간 조회
    public LocalDateTime getLastReadTime(User user) {
        if (user.equals(user1)) {
            return user1LastReadAt;
        } else if (user.equals(user2)) {
            return user2LastReadAt;
        }
        return null;
    }
}
