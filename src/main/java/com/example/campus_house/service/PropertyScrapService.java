package com.example.campus_house.service;

import com.example.campus_house.entity.Property;
import com.example.campus_house.entity.PropertyScrap;
import com.example.campus_house.entity.User;
import com.example.campus_house.repository.PropertyRepository;
import com.example.campus_house.repository.PropertyScrapRepository;
import com.example.campus_house.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PropertyScrapService {
    
    private final PropertyScrapRepository propertyScrapRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    
    // 매물 스크랩 토글 (간단한 버전)
    @Transactional
    public boolean toggleScrap(Long propertyId, Long userId) {
        return togglePropertyScrap(propertyId, userId);
    }
    
    // 매물 스크랩 토글
    @Transactional
    public boolean togglePropertyScrap(Long propertyId, Long userId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("매물을 찾을 수 없습니다."));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 이미 스크랩했는지 확인
        if (propertyScrapRepository.existsByUserIdAndPropertyId(userId, propertyId)) {
            // 스크랩 취소
            propertyScrapRepository.deleteByUserIdAndPropertyId(userId, propertyId);
            
            // 매물의 스크랩 수 감소
            property.setScrapCount(property.getScrapCount() - 1);
            propertyRepository.save(property);
            
            return false; // 스크랩 취소됨
        } else {
            // 스크랩 추가
            PropertyScrap scrap = PropertyScrap.builder()
                    .user(user)
                    .property(property)
                    .build();
            
            propertyScrapRepository.save(scrap);
            
            // 매물의 스크랩 수 증가
            property.setScrapCount(property.getScrapCount() + 1);
            propertyRepository.save(property);
            
            return true; // 스크랩 추가됨
        }
    }
    
    // 매물 스크랩 상태 확인
    public boolean isPropertyScraped(Long propertyId, Long userId) {
        return propertyScrapRepository.existsByUserIdAndPropertyId(userId, propertyId);
    }
    
    // 사용자의 스크랩한 매물 조회 (페이징)
    public Page<PropertyScrap> getScrapedPropertiesByUserId(Long userId, Pageable pageable) {
        return propertyScrapRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
    
    // 사용자의 스크랩한 매물 조회 (페이징 없음)
    public List<PropertyScrap> getAllScrapedPropertiesByUserId(Long userId) {
        return propertyScrapRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    // 특정 매물의 스크랩 수 조회
    public long getScrapCountByPropertyId(Long propertyId) {
        return propertyScrapRepository.countByPropertyId(propertyId);
    }
}
