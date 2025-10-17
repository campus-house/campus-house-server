package com.example.campus_house.entity;

public enum BoardType {
    APARTMENT("아파트소식"),
    QUESTION("질문게시판"),
    LOCAL("동네소식"),
    TRANSFER("양도게시판");

    private final String displayName;

    BoardType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
