package com.example.campus_house.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildingRatingStatsDto {
    private Double averageSatisfaction; // 만족도 평균 (1-5)
    private Long totalReviewCount; // 전체 후기 수
    private Double noisePercentage; // 소음 평균 퍼센트 (0-100)
    private Double facilityPercentage; // 편의시설 평균 퍼센트 (0-100)
    private Double parkingPercentage; // 주차장 평균 퍼센트 (0-100)
    private Double bugPercentage; // 벌레 평균 퍼센트 (0-100)
}
