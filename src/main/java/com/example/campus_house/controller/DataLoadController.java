package com.example.campus_house.controller;

import com.example.campus_house.service.BuildingDataLoaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
@Slf4j
public class DataLoadController {
    
    private final BuildingDataLoaderService buildingDataLoaderService;
    
    /**
     * 건물 데이터 로드
     */
    @PostMapping("/load/buildings")
    public ResponseEntity<Map<String, Object>> loadBuildings() {
        try {
            log.info("🏢 건물 데이터 로드 요청");
            
            buildingDataLoaderService.loadBuildingsFromJson();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "건물 데이터가 성공적으로 로드되었습니다.");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("건물 데이터 로드 실패", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "건물 데이터 로드에 실패했습니다: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 기존 데이터 삭제 (샘플 데이터 제외)
     */
    @DeleteMapping("/clear/buildings")
    public ResponseEntity<Map<String, Object>> clearBuildings() {
        try {
            log.info("🗑️ 건물 데이터 삭제 요청");
            
            buildingDataLoaderService.clearExistingData();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "기존 건물 데이터가 삭제되었습니다. (샘플 데이터 제외)");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("건물 데이터 삭제 실패", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "건물 데이터 삭제에 실패했습니다: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
}
