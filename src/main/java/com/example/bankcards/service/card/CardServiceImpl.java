package com.example.bankcards.service.card;

import com.example.bankcards.dto.CardCreateDto;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.StatusCode;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.card.CardRepository;
import com.example.bankcards.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public CardServiceImpl(CardRepository cardRepository, UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Card save(Card card) {
        return cardRepository.save(card);
    }

    @Transactional
    @Override
    public Card createCard(CardCreateDto cardCreateDto) {
        User user = userRepository.findById(cardCreateDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        Card card = new Card();
        card.setCardNumber(generateCardNumber());
        card.setUser(user);
        card.setExpiryDate(LocalDate.now().plusYears(10));
        card.setStatus(cardCreateDto.getStatus() != null ? cardCreateDto.getStatus() : StatusCode.ACTIVE);
        card.setBalance(cardCreateDto.getBalance() != null ? cardCreateDto.getBalance() : BigDecimal.ZERO);
        return cardRepository.save(card);
    }

    @Override
    public Optional<Card> findById(Long id) {
        return cardRepository.findById(id);
    }

    @Override
    public List<CardDto> getAllCards() {
        return cardRepository.findAll().stream().map(CardDto::fromEntity).toList();
    }

    @Override
    public boolean updateStatusCard(Long id, StatusCode statusCode) {
        return cardRepository.findById(id)
                .map(card -> {
                    card.setStatus(statusCode);
                    cardRepository.save(card);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public Page<CardDto> getUserCards(Long userId, Pageable pageable) {
        return cardRepository.findByUserId(userId, pageable)
                .map(CardDto::fromEntity);
    }

    @Override
    public boolean deleteCard(Long id) {
        return cardRepository.delete(id) > 0L;
    }

    @Override
    public BigDecimal getCardBalance(Long cardId) {
        return cardRepository.findById(cardId)
                .map(Card::getBalance)
                .orElseThrow(() -> new EntityNotFoundException("Card not found with id: " + cardId));
    }

    private String generateCardNumber() {
        return "400000" + String.format("%010d", (long) (Math.random() * 1_000_000_0000L));
    }
}
