package com.example.campus_house.controller;

import com.example.campus_house.entity.PropertyScrap;
import com.example.campus_house.entity.User;
import com.example.campus_house.service.AuthService;
import com.example.campus_house.service.PropertyScrapService;
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

    private final PropertyScrapService propertyScrapService;
    private final AuthService authService;

    // 사용자의 매물 스크랩 목록 (페이징)
    @GetMapping("/properties")
    public ResponseEntity<Page<PropertyScrap>> getMyPropertyScraps(
            @RequestHeader("Authorization") String token,
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        try {
            User user = authService.getUserFromToken(token.substring(7));
            Page<PropertyScrap> scraps = propertyScrapService.getScrapedPropertiesByUserId(user.getId(), pageable);
            return ResponseEntity.ok(scraps);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}


