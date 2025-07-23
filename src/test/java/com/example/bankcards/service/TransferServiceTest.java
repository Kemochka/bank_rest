package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.card.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransferServiceTest {

    private CardRepository cardRepository;
    private TransferService transferService;

    @BeforeEach
    void setUp() {
        cardRepository = mock(CardRepository.class);
        transferService = new TransferService(cardRepository);
    }

    @Test
    void transferBetweenOwnCards_ShouldTransferMoney_WhenValidRequest() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        Card fromCard = new Card();
        fromCard.setCardNumber("1111");
        fromCard.setUser(user);
        fromCard.setBalance(new BigDecimal("500"));

        Card toCard = new Card();
        toCard.setCardNumber("2222");
        toCard.setUser(user);
        toCard.setBalance(new BigDecimal("100"));

        TransferRequestDto request = new TransferRequestDto("1111", "2222", new BigDecimal("200"));

        when(cardRepository.findByCardNumber("1111")).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByCardNumber("2222")).thenReturn(Optional.of(toCard));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        transferService.transferBetweenOwnCards(request, userId);

        assertThat(fromCard.getBalance()).isEqualByComparingTo("300");
        assertThat(toCard.getBalance()).isEqualByComparingTo("300");

        verify(cardRepository).save(fromCard);
        verify(cardRepository).save(toCard);
    }

    @Test
    void transferBetweenOwnCards_ShouldThrow_WhenFromCardNotFound() {
        when(cardRepository.findByCardNumber("1111")).thenReturn(Optional.empty());

        TransferRequestDto request = new TransferRequestDto("1111", "2222", BigDecimal.TEN);

        assertThatThrownBy(() -> transferService.transferBetweenOwnCards(request, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Карта-отправителя не найдена");

        verify(cardRepository, never()).save(any());
    }

    @Test
    void transferBetweenOwnCards_ShouldThrow_WhenToCardNotFound() {
        Card fromCard = new Card();
        fromCard.setCardNumber("1111");
        fromCard.setUser(new User());
        when(cardRepository.findByCardNumber("1111")).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByCardNumber("2222")).thenReturn(Optional.empty());

        TransferRequestDto request = new TransferRequestDto("1111", "2222", BigDecimal.TEN);

        assertThatThrownBy(() -> transferService.transferBetweenOwnCards(request, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Карта-получатель не найдена");

        verify(cardRepository, never()).save(any());
    }

    @Test
    void transferBetweenOwnCards_ShouldThrow_WhenUserDoesNotOwnCards() {
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);

        Card fromCard = new Card();
        fromCard.setCardNumber("1111");
        fromCard.setUser(user1);
        fromCard.setBalance(BigDecimal.TEN);

        Card toCard = new Card();
        toCard.setCardNumber("2222");
        toCard.setUser(user2);
        toCard.setBalance(BigDecimal.ZERO);

        when(cardRepository.findByCardNumber("1111")).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByCardNumber("2222")).thenReturn(Optional.of(toCard));

        TransferRequestDto request = new TransferRequestDto("1111", "2222", BigDecimal.ONE);

        assertThatThrownBy(() -> transferService.transferBetweenOwnCards(request, 3L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Невозможен перевод между счетами");

        verify(cardRepository, never()).save(any());
    }

    @Test
    void transferBetweenOwnCards_ShouldThrow_WhenAmountIsNegative() {
        User user = new User();
        user.setId(1L);

        Card fromCard = new Card();
        fromCard.setCardNumber("1111");
        fromCard.setUser(user);
        fromCard.setBalance(BigDecimal.TEN);

        Card toCard = new Card();
        toCard.setCardNumber("2222");
        toCard.setUser(user);
        toCard.setBalance(BigDecimal.ZERO);

        when(cardRepository.findByCardNumber("1111")).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByCardNumber("2222")).thenReturn(Optional.of(toCard));

        TransferRequestDto request = new TransferRequestDto("1111", "2222", new BigDecimal("-5"));

        assertThatThrownBy(() -> transferService.transferBetweenOwnCards(request, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Невозможен перевод отрицательной суммы");

        verify(cardRepository, never()).save(any());
    }

    @Test
    void transferBetweenOwnCards_ShouldThrow_WhenInsufficientBalance() {
        User user = new User();
        user.setId(1L);

        Card fromCard = new Card();
        fromCard.setCardNumber("1111");
        fromCard.setUser(user);
        fromCard.setBalance(BigDecimal.TEN);

        Card toCard = new Card();
        toCard.setCardNumber("2222");
        toCard.setUser(user);
        toCard.setBalance(BigDecimal.ZERO);

        when(cardRepository.findByCardNumber("1111")).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByCardNumber("2222")).thenReturn(Optional.of(toCard));

        TransferRequestDto request = new TransferRequestDto("1111", "2222", new BigDecimal("20"));

        assertThatThrownBy(() -> transferService.transferBetweenOwnCards(request, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Недостаточно средств");

        verify(cardRepository, never()).save(any());
    }
}