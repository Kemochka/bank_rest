package com.example.bankcards.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum StatusCode {
    ACTIVE, BLOCKED, EXPIRED;

    @JsonCreator
    public static StatusCode from(String value) {
        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid operation type: " + value));
    }
}
