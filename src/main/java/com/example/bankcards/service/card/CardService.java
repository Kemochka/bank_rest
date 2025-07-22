package com.example.bankcards.service.card;

import com.example.bankcards.dto.CardCreateDto;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.StatusCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CardService {
    Card save(Card card);

    Card createCard(CardCreateDto cardCreateDto);

    Optional<Card> findById(Long id);

    List<CardDto> getAllCards();

    boolean updateStatusCard(Long id, StatusCode statusCode);

    Page<CardDto> getUserCards(Long userId, Pageable pageable);

    boolean deleteCard(Long id);

    BigDecimal getCardBalance(Long cardId);
}
