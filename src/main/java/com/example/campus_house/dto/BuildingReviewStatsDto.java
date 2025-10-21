package com.example.campus_house.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildingReviewStatsDto {
    private List<ReviewKeywordStatsDto> goodPoints; // 좋은 점 Top3
    private List<ReviewKeywordStatsDto> disappointingPoints; // 아쉬운 점 Top3
    private Long totalReviewCount; // 전체 후기 수
}
