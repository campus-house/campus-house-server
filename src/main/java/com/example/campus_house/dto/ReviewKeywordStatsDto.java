package com.example.campus_house.dto;

import com.example.campus_house.entity.ReviewKeyword;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewKeywordStatsDto {
    private ReviewKeyword keyword;
    private String description;
    private Long count;
    private Double percentage;
    
    public ReviewKeywordStatsDto(ReviewKeyword keyword, Long count, Long totalCount) {
        this.keyword = keyword;
        this.description = keyword.getDescription();
        this.count = count;
        this.percentage = totalCount > 0 ? Math.round((double) count / totalCount * 100 * 10.0) / 10.0 : 0.0;
    }
}
