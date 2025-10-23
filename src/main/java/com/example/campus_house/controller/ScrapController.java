package com.example.campus_house.controller;

import com.example.campus_house.entity.BuildingScrap;
import com.example.campus_house.entity.User;
import com.example.campus_house.service.AuthService;
import com.example.campus_house.service.BuildingScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scraps")
@RequiredArgsConstructor
public class ScrapController {

    private final BuildingScrapService buildingScrapService;
    private final AuthService authService;

    // 사용자의 건물 스크랩 목록 (페이징)
    @GetMapping("/buildings")
    public ResponseEntity<Page<BuildingScrap>> getMyBuildingScraps(
            @RequestHeader("Authorization") String token,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            Page<BuildingScrap> scraps = buildingScrapService.getScrapedBuildingsByUserId(user.getUserId(), pageable);
            return ResponseEntity.ok(scraps);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 건물 스크랩
    @PostMapping("/buildings/{buildingId}")
    public ResponseEntity<BuildingScrap> scrapBuilding(
            @RequestHeader("Authorization") String token,
            @PathVariable Long buildingId) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            BuildingScrap scrap = buildingScrapService.scrapBuilding(user.getUserId(), buildingId);
            return ResponseEntity.ok(scrap);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 건물 스크랩 취소
    @DeleteMapping("/buildings/{buildingId}")
    public ResponseEntity<Void> unscrapBuilding(
            @RequestHeader("Authorization") String token,
            @PathVariable Long buildingId) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            buildingScrapService.unscrapBuilding(user.getUserId(), buildingId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}


