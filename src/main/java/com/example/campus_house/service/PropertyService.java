package com.example.campus_house.service;

import com.example.campus_house.entity.Property;
import com.example.campus_house.repository.PropertyRepository;
// import com.example.campus_house.service.NaverApiService; // 현재 사용하지 않음
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PropertyService {
    
    private final PropertyRepository propertyRepository;
    private final NaverApiService naverApiService;
    
    // 위치 기반 매물 검색 (반경 내)
    @Cacheable(value = "properties", key = "#latitude + '_' + #longitude + '_' + #radiusKm")
    public List<Property> findPropertiesWithinRadius(double latitude, double longitude, double radiusKm) {
        return propertyRepository.findPropertiesWithinRadius(latitude, longitude, radiusKm);
    }
    
    // 건물명으로 검색
    public Page<Property> searchByBuildingName(String buildingName, Pageable pageable) {
        return propertyRepository.findByBuildingNameContainingIgnoreCaseAndStatusOrderByCreatedAtDesc(
                buildingName, Property.PropertyStatus.AVAILABLE, pageable);
    }
    
    // 주소로 검색
    public Page<Property> searchByAddress(String address, Pageable pageable) {
        return propertyRepository.findByAddressContainingIgnoreCaseAndStatusOrderByCreatedAtDesc(
                address, Property.PropertyStatus.AVAILABLE, pageable);
    }
    
    // 키워드 검색 (건물명 + 주소)
    public Page<Property> searchByKeyword(String keyword, Pageable pageable) {
        return propertyRepository.findByKeyword(keyword, Property.PropertyStatus.AVAILABLE, pageable);
    }
    
    // 매물 타입별 검색
    public Page<Property> searchByPropertyType(Property.PropertyType propertyType, Pageable pageable) {
        return propertyRepository.findByPropertyTypeAndStatusOrderByCreatedAtDesc(
                propertyType, Property.PropertyStatus.AVAILABLE, pageable);
    }
    
    // 층수 타입별 검색
    public Page<Property> searchByFloorType(Property.FloorType floorType, Pageable pageable) {
        return propertyRepository.findByFloorTypeAndStatusOrderByCreatedAtDesc(
                floorType, Property.PropertyStatus.AVAILABLE, pageable);
    }
    
    // 가격 범위 검색
    public Page<Property> searchByDepositRange(BigDecimal minDeposit, BigDecimal maxDeposit, Pageable pageable) {
        return propertyRepository.findByDepositRange(minDeposit, maxDeposit, Property.PropertyStatus.AVAILABLE, pageable);
    }
    
    // 월세 범위 검색
    public Page<Property> searchByMonthlyRentRange(BigDecimal minRent, BigDecimal maxRent, Pageable pageable) {
        return propertyRepository.findByMonthlyRentRange(minRent, maxRent, Property.PropertyStatus.AVAILABLE, pageable);
    }
    
    // 복합 필터 검색
    public Page<Property> searchWithFilters(Property.PropertyType propertyType,
                                          Property.FloorType floorType,
                                          BigDecimal minDeposit,
                                          BigDecimal maxDeposit,
                                          BigDecimal minRent,
                                          BigDecimal maxRent,
                                          Pageable pageable) {
        return propertyRepository.findPropertiesWithFilters(
                propertyType, floorType, minDeposit, maxDeposit, minRent, maxRent,
                Property.PropertyStatus.AVAILABLE, pageable);
    }
    
    // 인기 매물 조회
    public Page<Property> getPopularProperties(Pageable pageable) {
        return propertyRepository.findPopularProperties(Property.PropertyStatus.AVAILABLE, pageable);
    }
    
    // 최근 등록된 매물 조회
    public Page<Property> getRecentProperties(Pageable pageable) {
        return propertyRepository.findByStatusOrderByCreatedAtDesc(Property.PropertyStatus.AVAILABLE, pageable);
    }
    
    // 매물 상세 조회
    @Transactional
    public Property getPropertyById(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("매물을 찾을 수 없습니다."));
        
        // 조회수 증가
        property.setViewCount(property.getViewCount() + 1);
        propertyRepository.save(property);
        
        return property;
    }
    
    // 매물 생성
    @Transactional
    public Property createProperty(Property property) {
        // 주소를 좌표로 변환
        naverApiService.getCoordinatesFromAddress(property.getAddress())
                .subscribe(coordinates -> {
                    property.setLatitude(coordinates.get("latitude"));
                    property.setLongitude(coordinates.get("longitude"));
                });
        
        return propertyRepository.save(property);
    }
    
    // 매물 수정
    @Transactional
    public Property updateProperty(Long propertyId, Property updatedProperty) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("매물을 찾을 수 없습니다."));
        
        // 주소가 변경된 경우 좌표 재계산
        if (!property.getAddress().equals(updatedProperty.getAddress())) {
            naverApiService.getCoordinatesFromAddress(updatedProperty.getAddress())
                    .subscribe(coordinates -> {
                        updatedProperty.setLatitude(coordinates.get("latitude"));
                        updatedProperty.setLongitude(coordinates.get("longitude"));
                    });
        }
        
        // 매물 정보 업데이트
        property.setBuildingName(updatedProperty.getBuildingName());
        property.setAddress(updatedProperty.getAddress());
        property.setDetailAddress(updatedProperty.getDetailAddress());
        property.setPropertyType(updatedProperty.getPropertyType());
        property.setDeposit(updatedProperty.getDeposit());
        property.setMonthlyRent(updatedProperty.getMonthlyRent());
        property.setManagementFee(updatedProperty.getManagementFee());
        property.setFloor(updatedProperty.getFloor());
        property.setFloorType(updatedProperty.getFloorType());
        property.setArea(updatedProperty.getArea());
        property.setRooms(updatedProperty.getRooms());
        property.setBathrooms(updatedProperty.getBathrooms());
        property.setStructure(updatedProperty.getStructure());
        property.setDescription(updatedProperty.getDescription());
        property.setOptions(updatedProperty.getOptions());
        property.setContactInfo(updatedProperty.getContactInfo());
        property.setAgentName(updatedProperty.getAgentName());
        property.setAgentPhone(updatedProperty.getAgentPhone());
        
        return propertyRepository.save(property);
    }
    
    // 매물 삭제
    @Transactional
    public void deleteProperty(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("매물을 찾을 수 없습니다."));
        
        property.setStatus(Property.PropertyStatus.RENTED);
        propertyRepository.save(property);
    }
    
    // 네이버 API를 통한 지역 검색
    public Map<String, Object> searchPlaces(String query, int display) {
        return naverApiService.searchPlaces(query, display).block();
    }
    
    // 주소를 좌표로 변환
    public Map<String, Double> getCoordinatesFromAddress(String address) {
        return naverApiService.getCoordinatesFromAddress(address).block();
    }
    
    // 좌표를 주소로 변환
    public String getAddressFromCoordinates(double latitude, double longitude) {
        return naverApiService.getAddressFromCoordinates(latitude, longitude).block();
    }
    
    // 두 좌표 간의 거리 계산
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        return naverApiService.calculateDistance(lat1, lon1, lat2, lon2);
    }
}
