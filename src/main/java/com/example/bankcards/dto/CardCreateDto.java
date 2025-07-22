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
public class CardCreateDto {
    private Long id;
    private String maskedCardNumber;
    private Long userId;
    private LocalDate expiryDate;
    private StatusCode status;
    private BigDecimal balance;

    public static CardCreateDto fromEntity(Card card) {
        CardCreateDto dto = new CardCreateDto();
        dto.id = card.getId();
        dto.maskedCardNumber = MaskedCardNumber.maskCardNumber(card.getCardNumber());
        dto.userId = UserDto.fromUser(card.getUser()).getId();
        dto.expiryDate = card.getExpiryDate();
        dto.status = card.getStatus();
        dto.balance = card.getBalance();
        return dto;
    }
}
