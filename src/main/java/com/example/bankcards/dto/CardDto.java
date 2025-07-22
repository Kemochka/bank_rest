package com.example.bankcards.dto;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.StatusCode;
import com.example.bankcards.util.MaskedCardNumber;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardDto {
    private Long id;
    private String maskedCardNumber;
    private UserDto user;
    private LocalDate expiryDate;
    private StatusCode status;
    private BigDecimal balance;

    public static CardDto fromEntity(Card card) {
        CardDto dto = new CardDto();
        dto.id = card.getId();
        dto.maskedCardNumber = MaskedCardNumber.maskCardNumber(card.getCardNumber());
        dto.user = UserDto.fromUser(card.getUser());
        dto.expiryDate = card.getExpiryDate();
        dto.status = card.getStatus();
        dto.balance = card.getBalance();
        return dto;
    }
}
