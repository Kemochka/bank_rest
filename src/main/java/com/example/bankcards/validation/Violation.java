package com.example.bankcards.validation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Violation {
    @Schema(description = "Field name")
    private final String fieldName;
    @Schema(description = "Validation message")
    private final String message;
}
