package com.example.bankcards.service.card;

import com.example.bankcards.dto.CardCreateDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.StatusCode;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.card.CardRepository;
import com.example.bankcards.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CardServiceImplTest {

    private CardRepository cardRepository;
    private UserRepository userRepository;
    private CardServiceImpl cardService;

    @BeforeEach
    void setUp() {
        cardRepository = mock(CardRepository.class);
        userRepository = mock(UserRepository.class);
        cardService = new CardServiceImpl(cardRepository, userRepository);
    }

    @Test
    void save_ShouldReturnSavedCard() {
        Card card = new Card();
        when(cardRepository.save(card)).thenReturn(card);
        Card result = cardService.save(card);
        assertThat(result).isEqualTo(card);
        verify(cardRepository).save(card);
    }

    @Test
    void createCard_ShouldCreateAndSaveCard_WhenUserExists() {
        Long userId = 1L;
        User user = new User();
        CardCreateDto dto = new CardCreateDto();
        dto.setUserId(userId);
        dto.setStatus(StatusCode.ACTIVE);
        dto.setBalance(new BigDecimal("100.00"));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cardRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        Card createdCard = cardService.createCard(dto);
        assertThat(createdCard.getUser()).isEqualTo(user);
        assertThat(createdCard.getStatus()).isEqualTo(StatusCode.ACTIVE);
        assertThat(createdCard.getBalance()).isEqualByComparingTo("100.00");
        assertThat(createdCard.getExpiryDate()).isAfter(LocalDate.now());
        assertThat(createdCard.getCardNumber()).startsWith("400000");
        verify(userRepository).findById(userId);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void createCard_ShouldThrow_WhenUserNotFound() {
        CardCreateDto dto = new CardCreateDto();
        dto.setUserId(999L);
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> cardService.createCard(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Пользователь не найден");
        verify(userRepository).findById(999L);
        verifyNoInteractions(cardRepository);
    }

    @Test
    void findById_ShouldReturnCardOptional() {
        Card card = new Card();
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        Optional<Card> result = cardService.findById(1L);
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(card);
        verify(cardRepository).findById(1L);
    }

    @Test
    void updateStatusCard_ShouldUpdateAndReturnTrue_WhenCardExists() {
        Card card = new Card();
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);
        boolean updated = cardService.updateStatusCard(1L, StatusCode.BLOCKED);
        assertThat(updated).isTrue();
        assertThat(card.getStatus()).isEqualTo(StatusCode.BLOCKED);
        verify(cardRepository).findById(1L);
        verify(cardRepository).save(card);
    }

    @Test
    void updateStatusCard_ShouldReturnFalse_WhenCardNotFound() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());
        boolean updated = cardService.updateStatusCard(1L, StatusCode.BLOCKED);
        assertThat(updated).isFalse();
        verify(cardRepository).findById(1L);
        verify(cardRepository, never()).save(any());
    }

    @Test
    void deleteCard_ShouldReturnTrue_WhenDeleted() {
        when(cardRepository.delete(1L)).thenReturn(1);
        boolean result = cardService.deleteCard(1L);
        assertThat(result).isTrue();
        verify(cardRepository).delete(1L);
    }

    @Test
    void deleteCard_ShouldReturnFalse_WhenNothingDeleted() {
        when(cardRepository.delete(1L)).thenReturn(0);
        boolean result = cardService.deleteCard(1L);
        assertThat(result).isFalse();
        verify(cardRepository).delete(1L);
    }

    @Test
    void getCardBalance_ShouldReturnBalance_WhenCardExists() {
        Card card = new Card();
        card.setBalance(new BigDecimal("150.50"));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        BigDecimal balance = cardService.getCardBalance(1L);
        assertThat(balance).isEqualByComparingTo("150.50");
        verify(cardRepository).findById(1L);
    }

    @Test
    void getCardBalance_ShouldThrow_WhenCardNotFound() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> cardService.getCardBalance(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Card not found with id: 1");
        verify(cardRepository).findById(1L);
    }
}