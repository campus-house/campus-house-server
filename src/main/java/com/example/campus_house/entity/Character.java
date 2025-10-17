package com.example.campus_house.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "characters")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Character {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name; // 캐릭터 이름
    
    @Column(columnDefinition = "TEXT")
    private String description; // 캐릭터 설명
    
    @Column(nullable = false)
    private String imageUrl; // 캐릭터 이미지 URL
    
    @Column(nullable = false)
    private Integer price; // 가격 (리워드)
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true; // 활성화 여부
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
}
