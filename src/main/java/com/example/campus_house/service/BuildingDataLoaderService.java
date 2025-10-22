package com.example.campus_house.service;

import com.example.campus_house.entity.Building;
import com.example.campus_house.repository.BuildingRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BuildingDataLoaderService {
    
    private final BuildingRepository buildingRepository;
    private final ObjectMapper objectMapper;
    
    /**
     * JSON 파일에서 건물 데이터를 로드하여 데이터베이스에 저장
     */
    @Transactional
    public void loadBuildingsFromJson() {
        try {
            log.info("🏢 건물 데이터 로드 시작");
            
            // 기존 샘플 데이터가 아닌 실제 데이터 삭제
            clearExistingData();
            
            // JSON 파일 읽기
            List<Map<String, Object>> buildingsData = readJsonFile();
            
            if (buildingsData.isEmpty()) {
                log.warn("로드할 건물 데이터가 없습니다.");
                return;
            }
            
            log.info("📄 JSON 파일에서 {}개 건물 데이터 읽기 완료", buildingsData.size());
            
            // 건물 데이터 변환 및 저장
            int successCount = 0;
            for (Map<String, Object> buildingData : buildingsData) {
                try {
                    Building building = convertToBuilding(buildingData);
                    buildingRepository.save(building);
                    successCount++;
                    
                    if (successCount % 50 == 0) {
                        log.info("📊 진행상황: {}/{} ({}%)", 
                            successCount, buildingsData.size(), 
                            successCount * 100 / buildingsData.size());
                    }
                } catch (Exception e) {
                    log.error("건물 데이터 변환 실패: {}", buildingData.get("building_name"), e);
                }
            }
            
            log.info("✅ 건물 데이터 로드 완료!");
            log.info("  - 성공: {}개", successCount);
            log.info("  - 실패: {}개", buildingsData.size() - successCount);
            log.info("  - 성공률: {}%", successCount * 100 / buildingsData.size());
            
        } catch (Exception e) {
            log.error("건물 데이터 로드 중 오류 발생", e);
            throw new RuntimeException("건물 데이터 로드 실패", e);
        }
    }
    
    /**
     * 기존 데이터 삭제 (샘플 데이터 제외)
     */
    @Transactional
    public void clearExistingData() {
        try {
            // 샘플 데이터는 유지하고 실제 데이터만 삭제
            int deletedCount = buildingRepository.deleteByBuildingNameNotIn(
                List.of("캠퍼스 하우스 A동", "캠퍼스 하우스 B동", "하이빌 영통")
            );
            log.info("🗑️ 기존 데이터 삭제 완료: {}개 건물", deletedCount);
        } catch (Exception e) {
            log.error("기존 데이터 삭제 실패", e);
        }
    }
    
    /**
     * JSON 파일 읽기
     */
    private List<Map<String, Object>> readJsonFile() throws IOException {
        ClassPathResource resource = new ClassPathResource("data/buildings/processed/buildings_processed.json");
        
        if (!resource.exists()) {
            log.warn("JSON 파일을 찾을 수 없습니다: {}", resource.getPath());
            return List.of();
        }
        
        return objectMapper.readValue(
            resource.getInputStream(),
            new TypeReference<List<Map<String, Object>>>() {}
        );
    }
    
    /**
     * JSON 데이터를 Building 엔티티로 변환
     */
    private Building convertToBuilding(Map<String, Object> data) {
        // JSON 파일에서 실제 좌표와 도보 시간 데이터 사용
        String address = (String) data.get("address");
        Double latitude = getDoubleValue(data.get("latitude"));
        Double longitude = getDoubleValue(data.get("longitude"));
        Integer schoolWalkingTime = getIntegerValue(data.get("school_walking_time"));
        Integer stationWalkingTime = getIntegerValue(data.get("station_walking_time"));
        
        // 기본값 설정
        String buildingName = (String) data.get("building_name");
        if (buildingName == null || buildingName.trim().isEmpty()) {
            buildingName = "알 수 없는 건물";
        }
        
        // 좌표가 없는 경우 기본 좌표 사용 (수원시 영통구 중심)
        if (latitude == null || longitude == null || latitude == 0.0 || longitude == 0.0) {
            latitude = 37.2636;
            longitude = 127.0286;
        }
        
        // 도보 시간이 없는 경우 기본값 설정
        if (schoolWalkingTime == null || schoolWalkingTime == 0) {
            schoolWalkingTime = 10; // 기본 10분
        }
        
        if (stationWalkingTime == null || stationWalkingTime == 0) {
            stationWalkingTime = Math.max(3, schoolWalkingTime - 2);
        }
        
        return Building.builder()
            .buildingName(buildingName)
            .address(address != null ? address : "주소 정보 없음")
            .latitude(latitude)
            .longitude(longitude)
            .deposit(convertToBigDecimal(getDoubleValue(data.get("avg_deposit")) * 10000))
            .monthlyRent(convertToBigDecimal(getDoubleValue(data.get("avg_monthly_rent")) * 10000))
            .jeonse(null) // 전세는 별도 계산 필요
            .households(getIntegerValue(data.get("households")))
            .heatingType((String) data.getOrDefault("heating_type", "개별난방"))
            .elevators(getIntegerValue(data.get("elevators")))
            .buildingUsage((String) data.getOrDefault("building_usage", "기타"))
            .nearbyConvenienceStores(0) // 나중에 업데이트
            .nearbyMarts(0)
            .nearbyHospitals(0)
            .schoolWalkingTime(schoolWalkingTime)
            .stationWalkingTime(stationWalkingTime)
            .scrapCount(0)
            .floorsGround(getIntegerValue(data.get("ground_floors")))
            .area(getDoubleValue(data.get("area")))
            .constructionYear(getIntegerValue(data.get("construction_year")))
            .roadName((String) data.getOrDefault("road_name", ""))
            .sampleCount(getIntegerValue(data.get("sample_count")))
            .avgPrice(convertToBigDecimal(getDoubleValue(data.get("avg_deposit")) * 10000))
            .build();
    }
    
    
    /**
     * Double 값 안전하게 가져오기
     */
    private Double getDoubleValue(Object value) {
        if (value == null) return 0.0;
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    /**
     * Integer 값 안전하게 가져오기
     */
    private Integer getIntegerValue(Object value) {
        if (value == null) return 0;
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    /**
     * BigDecimal 변환
     */
    private BigDecimal convertToBigDecimal(Double value) {
        if (value == null || value.isNaN()) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(value);
    }
}
