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
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class BuildingDataLoaderService {
    
    private final BuildingRepository buildingRepository;
    private final ObjectMapper objectMapper;
    private final Random random = new Random();
    
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
        // 주소에서 좌표 계산
        String address = (String) data.get("address");
        double[] coordinates = calculateCoordinates(address);
        
        // 건물 타입과 면적을 기반으로 걸리는 시간 계산
        String buildingType = (String) data.get("building_type");
        Double area = getDoubleValue(data.get("area"));
        int schoolTime = calculateWalkingTime(buildingType, area);
        int stationTime = Math.max(3, schoolTime - 2);
        
        // 기본값 설정
        String buildingName = (String) data.get("building_name");
        if (buildingName == null || buildingName.trim().isEmpty()) {
            buildingName = "알 수 없는 건물";
        }
        
        return Building.builder()
            .buildingName(buildingName)
            .address(address != null ? address : "주소 정보 없음")
            .latitude(coordinates[0])
            .longitude(coordinates[1])
            .deposit(convertToBigDecimal(getDoubleValue(data.get("avg_deposit")) * 10000))
            .monthlyRent(convertToBigDecimal(getDoubleValue(data.get("avg_monthly_rent")) * 10000))
            .jeonse(null) // 전세는 별도 계산 필요
            .households(getIntegerValue(data.get("households")))
            .heatingType((String) data.getOrDefault("heating_type", "개별난방"))
            .parkingSpaces(getIntegerValue(data.get("parking_spaces")))
            .elevators(getIntegerValue(data.get("elevators")))
            .buildingUsage((String) data.getOrDefault("building_usage", "기타"))
            .nearbyConvenienceStores(0) // 나중에 업데이트
            .nearbyMarts(0)
            .nearbyHospitals(0)
            .schoolWalkingTime(schoolTime)
            .stationWalkingTime(stationTime)
            .scrapCount(0)
            .floorsGround(getIntegerValue(data.get("ground_floors")))
            .hasElevator(getIntegerValue(data.get("elevators")) > 0)
            .area(area)
            .constructionYear(getIntegerValue(data.get("construction_year")))
            .roadName((String) data.getOrDefault("road_name", ""))
            .sampleCount(getIntegerValue(data.get("sample_count")))
            .avgPrice(convertToBigDecimal(getDoubleValue(data.get("avg_deposit")) * 10000))
            .build();
    }
    
    /**
     * 주소를 기반으로 위도/경도 좌표 계산
     */
    private double[] calculateCoordinates(String address) {
        // 수원시 영통구의 대략적인 중심 좌표
        double baseLat = 37.2636;
        double baseLng = 127.0286;
        
        if (address == null) {
            return new double[]{baseLat, baseLng};
        }
        
        // 주소에 따라 약간의 오프셋 추가
        double latOffset, lngOffset;
        
        if (address.contains("영통동")) {
            latOffset = (address.hashCode() % 100) / 10000.0; // ±0.01도 범위
            lngOffset = ((address + "1").hashCode() % 100) / 10000.0;
        } else if (address.contains("서천동")) {
            latOffset = (address.hashCode() % 50) / 10000.0 + 0.005; // 약간 남쪽
            lngOffset = ((address + "2").hashCode() % 50) / 10000.0 + 0.005; // 약간 동쪽
        } else {
            latOffset = (address.hashCode() % 200) / 10000.0 - 0.01;
            lngOffset = ((address + "3").hashCode() % 200) / 10000.0 - 0.01;
        }
        
        return new double[]{baseLat + latOffset, baseLng + lngOffset};
    }
    
    /**
     * 건물 타입과 면적을 기반으로 걸리는 시간 계산
     */
    private int calculateWalkingTime(String buildingType, Double area) {
        int baseTime = 10; // 기본 10분
        
        // 건물 타입별 조정
        if ("아파트".equals(buildingType)) {
            baseTime += 2; // 아파트는 보통 조금 더 멀리
        } else if ("오피스텔".equals(buildingType)) {
            baseTime -= 1; // 오피스텔은 보통 중심가에 위치
        }
        
        // 면적별 조정 (큰 건물일수록 중심가에 위치할 가능성)
        if (area != null) {
            if (area > 50) {
                baseTime -= 2;
            } else if (area > 30) {
                baseTime -= 1;
            }
        }
        
        // 랜덤 요소 추가
        int randomFactor = random.nextInt(7) - 3; // -3 ~ +3
        
        return Math.max(3, Math.min(25, baseTime + randomFactor)); // 3-25분 범위
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
