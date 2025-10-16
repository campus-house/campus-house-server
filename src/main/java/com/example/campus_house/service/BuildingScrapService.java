package com.example.campus_house.service;

import com.example.campus_house.entity.Building;
import com.example.campus_house.entity.BuildingScrap;
import com.example.campus_house.entity.User;
import com.example.campus_house.repository.BuildingScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BuildingScrapService {
    
    private final BuildingScrapRepository buildingScrapRepository;
    private final BuildingService buildingService;
    
    // 건물 스크랩
    @Transactional
    public BuildingScrap scrapBuilding(Long userId, Long buildingId) {
        // 이미 스크랩했는지 확인
        Optional<BuildingScrap> existingScrap = buildingScrapRepository.findByUserIdAndBuildingId(userId, buildingId);
        if (existingScrap.isPresent()) {
            throw new RuntimeException("이미 스크랩한 건물입니다.");
        }
        
        // 건물 존재 확인
        Building building = buildingService.getBuildingById(buildingId)
                .orElseThrow(() -> new RuntimeException("건물을 찾을 수 없습니다."));
        
        // 스크랩 생성
        BuildingScrap scrap = BuildingScrap.builder()
                .userId(userId)
                .buildingId(buildingId)
                .building(building)
                .build();
        
        BuildingScrap savedScrap = buildingScrapRepository.save(scrap);
        
        // 건물의 스크랩 수 증가
        buildingService.incrementScrapCount(buildingId);
        
        return savedScrap;
    }
    
    // 건물 스크랩 취소
    @Transactional
    public void unscrapBuilding(Long userId, Long buildingId) {
        BuildingScrap scrap = buildingScrapRepository.findByUserIdAndBuildingId(userId, buildingId)
                .orElseThrow(() -> new RuntimeException("스크랩한 건물이 아닙니다."));
        
        buildingScrapRepository.delete(scrap);
        
        // 건물의 스크랩 수 감소
        buildingService.decrementScrapCount(buildingId);
    }
    
    // 사용자의 건물 스크랩 목록 조회 (페이징)
    public Page<BuildingScrap> getScrapedBuildingsByUserId(Long userId, Pageable pageable) {
        return buildingScrapRepository.findByUserId(userId, pageable);
    }
    
    // 사용자의 건물 스크랩 목록 조회 (리스트)
    public List<BuildingScrap> getScrapedBuildingsByUserId(Long userId) {
        return buildingScrapRepository.findByUserId(userId);
    }
    
    // 사용자가 특정 건물을 스크랩했는지 확인
    public boolean isScraped(Long userId, Long buildingId) {
        return buildingScrapRepository.findByUserIdAndBuildingId(userId, buildingId).isPresent();
    }
    
    // 사용자가 스크랩한 건물 ID 목록
    public List<Long> getScrapedBuildingIds(Long userId) {
        return buildingScrapRepository.findBuildingIdsByUserId(userId);
    }
    
    // 건물별 스크랩 수
    public Long getScrapCount(Long buildingId) {
        return buildingScrapRepository.countByBuildingId(buildingId);
    }
    
    // 사용자별 스크랩 수
    public Long getUserScrapCount(Long userId) {
        return buildingScrapRepository.countByUserId(userId);
    }
}
