package com.example.campus_house.entity;

public enum ResidencePeriod {
    BEFORE_2023("2023년 이전"),
    UNTIL_2024("2024년까지"),
    UNTIL_2025("2025년까지");
    
    private final String description;
    
    ResidencePeriod(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
