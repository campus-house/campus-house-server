package com.example.campus_house.entity;

public enum ReviewKeyword {
    // 교통 관련
    NEAR_STATION("역과 가까워요"),
    FAR_FROM_STATION("역과 멀어요"),
    NEAR_SCHOOL("학교와 가까워요"),
    FAR_FROM_SCHOOL("학교와 멀어요"),
    
    // 편의시설 관련
    CONVENIENT_FACILITIES("편의시설"),
    FAR_FROM_FACILITIES("편의시설이 멀어요"),
    
    // 인터넷 관련
    WIFI_AVAILABLE("와이파이"),
    NO_WIFI("와이파이X"),
    
    // 주차 관련
    PARKING_AVAILABLE("주차장"),
    NO_PARKING("주차장X"),
    
    // 엘리베이터 관련
    ELEVATOR_AVAILABLE("엘리베이터"),
    NO_ELEVATOR("엘리베이터X"),
    
    // 청결도 관련
    CLEAN("깨끗해요"),
    OLD("오래됐어요"),
    
    // 벌레 관련
    NO_BUGS("벌레가 안나와요"),
    MANY_BUGS("벌레가 많아요"),
    
    // 채광/습도 관련
    GOOD_LIGHTING("채광이 좋아요"),
    HUMID("습해요"),
    
    // 가격 관련
    GOOD_VALUE("가성비"),
    EXPENSIVE("비싸요"),
    
    // 소음 관련
    QUIET("소음이 적어요"),
    NOISY("소음이 있어요"),
    
    // 보안 관련
    SECURE("보안이 철저해요"),
    INSECURE("보안이 안좋아요");
    
    private final String description;
    
    ReviewKeyword(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
