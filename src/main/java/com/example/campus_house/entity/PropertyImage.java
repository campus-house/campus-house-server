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
@Table(name = "property_images")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PropertyImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;
    
    @Column(nullable = false)
    private String imageUrl;
    
    @Column
    private String originalFileName;
    
    @Column
    private Long fileSize;
    
    @Column
    private Integer orderIndex; // 이미지 순서
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageType imageType; // 이미지 타입
    
    @Column
    private String description; // 이미지 설명
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    public enum ImageType {
        EXTERIOR,   // 외관
        INTERIOR,   // 내부
        FACILITY,   // 시설
        ETC         // 기타
    }
}
