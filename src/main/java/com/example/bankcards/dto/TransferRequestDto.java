package com.example.bankcards.dto;

import java.math.BigDecimal;

public record TransferRequestDto(String fromCardNumber, String toCardNumber, BigDecimal amount) {
}
