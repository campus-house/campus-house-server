package com.example.campus_house.entity;

public enum ResidenceType {
    APARTMENT("아파트"),
    OFFICETEL("오피스텔"),
    HOUSE_VILLA("주택/빌라"),
    ONE_TWO_ROOM("원투룸");
    
    private final String description;
    
    ResidenceType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
