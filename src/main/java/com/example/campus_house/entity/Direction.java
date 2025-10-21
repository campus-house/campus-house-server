package com.example.campus_house.entity;

public enum Direction {
    SOUTH("남향"),
    SOUTH_EAST_WEST("남동/남서향"),
    EAST("동향"),
    WEST("서향"),
    NORTH("북향"),
    NORTH_SOUTH_EAST_WEST("남북/동서향");
    
    private final String description;
    
    Direction(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
