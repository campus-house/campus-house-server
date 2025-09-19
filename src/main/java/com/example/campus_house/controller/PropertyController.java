package com.example.campus_house.controller;

import com.example.campus_house.entity.Property;
import com.example.campus_house.entity.User;
import com.example.campus_house.service.PropertyService;
import com.example.campus_house.service.AuthService;
import com.example.campus_house.service.PropertyScrapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
@Tag(name = "매물", description = "매물 관련 API")
public class PropertyController {
    
    private final PropertyService propertyService;
    private final AuthService authService;
    private final PropertyScrapService propertyScrapService;
    
    // 위치 기반 매물 검색 (반경 내)
    @GetMapping("/nearby")
    public ResponseEntity<List<Property>> getPropertiesNearby(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "1.0") double radiusKm) {
        List<Property> properties = propertyService.findPropertiesWithinRadius(latitude, longitude, radiusKm);
        return ResponseEntity.ok(properties);
    }
    
    // 건물명으로 검색
    @GetMapping("/search/building")
    public ResponseEntity<Page<Property>> searchByBuildingName(
            @RequestParam String buildingName,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Property> properties = propertyService.searchByBuildingName(buildingName, pageable);
        return ResponseEntity.ok(properties);
    }
    
    // 주소로 검색
    @GetMapping("/search/address")
    public ResponseEntity<Page<Property>> searchByAddress(
            @RequestParam String address,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Property> properties = propertyService.searchByAddress(address, pageable);
        return ResponseEntity.ok(properties);
    }
    
    // 키워드 검색
    @GetMapping("/search")
    public ResponseEntity<Page<Property>> searchByKeyword(
            @RequestParam String keyword,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Property> properties = propertyService.searchByKeyword(keyword, pageable);
        return ResponseEntity.ok(properties);
    }
    
    // 매물 타입별 검색
    @GetMapping("/search/type")
    public ResponseEntity<Page<Property>> searchByPropertyType(
            @RequestParam String propertyType,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Property.PropertyType type = Property.PropertyType.valueOf(propertyType.toUpperCase());
            Page<Property> properties = propertyService.searchByPropertyType(type, pageable);
            return ResponseEntity.ok(properties);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 층수 타입별 검색
    @GetMapping("/search/floor")
    public ResponseEntity<Page<Property>> searchByFloorType(
            @RequestParam String floorType,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Property.FloorType type = Property.FloorType.valueOf(floorType.toUpperCase());
            Page<Property> properties = propertyService.searchByFloorType(type, pageable);
            return ResponseEntity.ok(properties);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 가격 범위 검색
    @GetMapping("/search/price")
    public ResponseEntity<Page<Property>> searchByPriceRange(
            @RequestParam(required = false) BigDecimal minDeposit,
            @RequestParam(required = false) BigDecimal maxDeposit,
            @RequestParam(required = false) BigDecimal minRent,
            @RequestParam(required = false) BigDecimal maxRent,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Property> properties;
        
        if (minDeposit != null && maxDeposit != null) {
            properties = propertyService.searchByDepositRange(minDeposit, maxDeposit, pageable);
        } else if (minRent != null && maxRent != null) {
            properties = propertyService.searchByMonthlyRentRange(minRent, maxRent, pageable);
        } else {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(properties);
    }
    
    // 복합 필터 검색
    @GetMapping("/search/filters")
    public ResponseEntity<Page<Property>> searchWithFilters(
            @RequestParam(required = false) String propertyType,
            @RequestParam(required = false) String floorType,
            @RequestParam(required = false) BigDecimal minDeposit,
            @RequestParam(required = false) BigDecimal maxDeposit,
            @RequestParam(required = false) BigDecimal minRent,
            @RequestParam(required = false) BigDecimal maxRent,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Property.PropertyType pType = propertyType != null ? 
                    Property.PropertyType.valueOf(propertyType.toUpperCase()) : null;
            Property.FloorType fType = floorType != null ? 
                    Property.FloorType.valueOf(floorType.toUpperCase()) : null;
            
            Page<Property> properties = propertyService.searchWithFilters(
                    pType, fType, minDeposit, maxDeposit, minRent, maxRent, pageable);
            return ResponseEntity.ok(properties);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 인기 매물 조회
    @GetMapping("/popular")
    public ResponseEntity<Page<Property>> getPopularProperties(
            @PageableDefault(size = 20, sort = "scrapCount", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Property> properties = propertyService.getPopularProperties(pageable);
        return ResponseEntity.ok(properties);
    }
    
    // 최근 등록된 매물 조회
    @GetMapping("/recent")
    public ResponseEntity<Page<Property>> getRecentProperties(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Property> properties = propertyService.getRecentProperties(pageable);
        return ResponseEntity.ok(properties);
    }
    
    // 매물 상세 조회
    @GetMapping("/{propertyId}")
    public ResponseEntity<Property> getPropertyById(@PathVariable Long propertyId) {
        try {
            Property property = propertyService.getPropertyById(propertyId);
            return ResponseEntity.ok(property);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 매물 생성
    @PostMapping
    public ResponseEntity<Property> createProperty(@RequestBody CreatePropertyRequest request) {
        try {
            Property property = Property.builder()
                    .buildingName(request.getBuildingName())
                    .address(request.getAddress())
                    .detailAddress(request.getDetailAddress())
                    .propertyType(request.getPropertyType())
                    .deposit(request.getDeposit())
                    .monthlyRent(request.getMonthlyRent())
                    .managementFee(request.getManagementFee())
                    .floor(request.getFloor())
                    .floorType(request.getFloorType())
                    .area(request.getArea())
                    .rooms(request.getRooms())
                    .bathrooms(request.getBathrooms())
                    .structure(request.getStructure())
                    .description(request.getDescription())
                    .options(request.getOptions())
                    .contactInfo(request.getContactInfo())
                    .agentName(request.getAgentName())
                    .agentPhone(request.getAgentPhone())
                    .status(Property.PropertyStatus.AVAILABLE)
                    .viewCount(0)
                    .scrapCount(0)
                    .build();
            
            Property savedProperty = propertyService.createProperty(property);
            return ResponseEntity.ok(savedProperty);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 매물 수정
    @PutMapping("/{propertyId}")
    public ResponseEntity<Property> updateProperty(
            @PathVariable Long propertyId,
            @RequestBody UpdatePropertyRequest request) {
        try {
            Property updatedProperty = Property.builder()
                    .buildingName(request.getBuildingName())
                    .address(request.getAddress())
                    .detailAddress(request.getDetailAddress())
                    .propertyType(request.getPropertyType())
                    .deposit(request.getDeposit())
                    .monthlyRent(request.getMonthlyRent())
                    .managementFee(request.getManagementFee())
                    .floor(request.getFloor())
                    .floorType(request.getFloorType())
                    .area(request.getArea())
                    .rooms(request.getRooms())
                    .bathrooms(request.getBathrooms())
                    .structure(request.getStructure())
                    .description(request.getDescription())
                    .options(request.getOptions())
                    .contactInfo(request.getContactInfo())
                    .agentName(request.getAgentName())
                    .agentPhone(request.getAgentPhone())
                    .build();
            
            Property property = propertyService.updateProperty(propertyId, updatedProperty);
            return ResponseEntity.ok(property);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 매물 삭제
    @DeleteMapping("/{propertyId}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long propertyId) {
        try {
            propertyService.deleteProperty(propertyId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 네이버 API - 지역 검색
    @GetMapping("/search/places")
    public ResponseEntity<Map<String, Object>> searchPlaces(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int display) {
        Map<String, Object> result = propertyService.searchPlaces(query, display);
        return ResponseEntity.ok(result);
    }
    
    // 네이버 API - 주소를 좌표로 변환
    @GetMapping("/geocode")
    public ResponseEntity<Map<String, Double>> getCoordinatesFromAddress(@RequestParam String address) {
        Map<String, Double> coordinates = propertyService.getCoordinatesFromAddress(address);
        return ResponseEntity.ok(coordinates);
    }
    
    // 네이버 API - 좌표를 주소로 변환
    @GetMapping("/reverse-geocode")
    public ResponseEntity<String> getAddressFromCoordinates(
            @RequestParam double latitude,
            @RequestParam double longitude) {
        String address = propertyService.getAddressFromCoordinates(latitude, longitude);
        return ResponseEntity.ok(address);
    }
    
    // 거리 계산
    @GetMapping("/distance")
    public ResponseEntity<Double> calculateDistance(
            @RequestParam double lat1,
            @RequestParam double lon1,
            @RequestParam double lat2,
            @RequestParam double lon2) {
        double distance = propertyService.calculateDistance(lat1, lon1, lat2, lon2);
        return ResponseEntity.ok(distance);
    }
    
    // 매물 스크랩 토글
    @Operation(summary = "매물 스크랩 토글", description = "매물을 스크랩에 추가하거나 제거합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "스크랩 토글 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "매물을 찾을 수 없음")
    })
    @PostMapping("/{propertyId}/scrap")
    public ResponseEntity<String> toggleScrap(
            @Parameter(description = "매물 ID", required = true)
            @PathVariable Long propertyId,
            @RequestHeader("Authorization") String token) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            boolean isScrapped = propertyScrapService.toggleScrap(propertyId, user.getId());
            String message = isScrapped ? "매물을 스크랩했습니다." : "스크랩을 취소했습니다.";
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("스크랩 처리 중 오류가 발생했습니다.");
        }
    }
    
    // DTO 클래스들
    public static class CreatePropertyRequest {
        private String buildingName;
        private String address;
        private String detailAddress;
        private Property.PropertyType propertyType;
        private BigDecimal deposit;
        private BigDecimal monthlyRent;
        private BigDecimal managementFee;
        private Integer floor;
        private Property.FloorType floorType;
        private Double area;
        private Integer rooms;
        private Integer bathrooms;
        private String structure;
        private String description;
        private String options;
        private String contactInfo;
        private String agentName;
        private String agentPhone;
        
        // Getters and Setters
        public String getBuildingName() { return buildingName; }
        public void setBuildingName(String buildingName) { this.buildingName = buildingName; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getDetailAddress() { return detailAddress; }
        public void setDetailAddress(String detailAddress) { this.detailAddress = detailAddress; }
        public Property.PropertyType getPropertyType() { return propertyType; }
        public void setPropertyType(Property.PropertyType propertyType) { this.propertyType = propertyType; }
        public BigDecimal getDeposit() { return deposit; }
        public void setDeposit(BigDecimal deposit) { this.deposit = deposit; }
        public BigDecimal getMonthlyRent() { return monthlyRent; }
        public void setMonthlyRent(BigDecimal monthlyRent) { this.monthlyRent = monthlyRent; }
        public BigDecimal getManagementFee() { return managementFee; }
        public void setManagementFee(BigDecimal managementFee) { this.managementFee = managementFee; }
        public Integer getFloor() { return floor; }
        public void setFloor(Integer floor) { this.floor = floor; }
        public Property.FloorType getFloorType() { return floorType; }
        public void setFloorType(Property.FloorType floorType) { this.floorType = floorType; }
        public Double getArea() { return area; }
        public void setArea(Double area) { this.area = area; }
        public Integer getRooms() { return rooms; }
        public void setRooms(Integer rooms) { this.rooms = rooms; }
        public Integer getBathrooms() { return bathrooms; }
        public void setBathrooms(Integer bathrooms) { this.bathrooms = bathrooms; }
        public String getStructure() { return structure; }
        public void setStructure(String structure) { this.structure = structure; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getOptions() { return options; }
        public void setOptions(String options) { this.options = options; }
        public String getContactInfo() { return contactInfo; }
        public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
        public String getAgentName() { return agentName; }
        public void setAgentName(String agentName) { this.agentName = agentName; }
        public String getAgentPhone() { return agentPhone; }
        public void setAgentPhone(String agentPhone) { this.agentPhone = agentPhone; }
    }
    
    public static class UpdatePropertyRequest {
        private String buildingName;
        private String address;
        private String detailAddress;
        private Property.PropertyType propertyType;
        private BigDecimal deposit;
        private BigDecimal monthlyRent;
        private BigDecimal managementFee;
        private Integer floor;
        private Property.FloorType floorType;
        private Double area;
        private Integer rooms;
        private Integer bathrooms;
        private String structure;
        private String description;
        private String options;
        private String contactInfo;
        private String agentName;
        private String agentPhone;
        
        // Getters and Setters (same as CreatePropertyRequest)
        public String getBuildingName() { return buildingName; }
        public void setBuildingName(String buildingName) { this.buildingName = buildingName; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getDetailAddress() { return detailAddress; }
        public void setDetailAddress(String detailAddress) { this.detailAddress = detailAddress; }
        public Property.PropertyType getPropertyType() { return propertyType; }
        public void setPropertyType(Property.PropertyType propertyType) { this.propertyType = propertyType; }
        public BigDecimal getDeposit() { return deposit; }
        public void setDeposit(BigDecimal deposit) { this.deposit = deposit; }
        public BigDecimal getMonthlyRent() { return monthlyRent; }
        public void setMonthlyRent(BigDecimal monthlyRent) { this.monthlyRent = monthlyRent; }
        public BigDecimal getManagementFee() { return managementFee; }
        public void setManagementFee(BigDecimal managementFee) { this.managementFee = managementFee; }
        public Integer getFloor() { return floor; }
        public void setFloor(Integer floor) { this.floor = floor; }
        public Property.FloorType getFloorType() { return floorType; }
        public void setFloorType(Property.FloorType floorType) { this.floorType = floorType; }
        public Double getArea() { return area; }
        public void setArea(Double area) { this.area = area; }
        public Integer getRooms() { return rooms; }
        public void setRooms(Integer rooms) { this.rooms = rooms; }
        public Integer getBathrooms() { return bathrooms; }
        public void setBathrooms(Integer bathrooms) { this.bathrooms = bathrooms; }
        public String getStructure() { return structure; }
        public void setStructure(String structure) { this.structure = structure; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getOptions() { return options; }
        public void setOptions(String options) { this.options = options; }
        public String getContactInfo() { return contactInfo; }
        public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
        public String getAgentName() { return agentName; }
        public void setAgentName(String agentName) { this.agentName = agentName; }
        public String getAgentPhone() { return agentPhone; }
        public void setAgentPhone(String agentPhone) { this.agentPhone = agentPhone; }
    }
}
