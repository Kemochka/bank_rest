package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.repository.card.CardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransferService {
    private final CardRepository cardRepository;

    public TransferService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Transactional
    public void transferBetweenOwnCards(TransferRequestDto request, Long userId) {
        Card fromCard = cardRepository.findByCardNumber(request.fromCardNumber())
                .orElseThrow(() -> new IllegalArgumentException("Карта-отправителя не найдена"));
        Card toCard = cardRepository.findByCardNumber(request.toCardNumber())
                .orElseThrow(() -> new IllegalArgumentException("Карта-получатель не найдена"));
        if (!fromCard.getUser().getId().equals(userId)
                && !toCard.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Невозможен перевод между счетами, проверьте данные карты");
        }

        if (request.amount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Невозможен перевод отрицательной суммы");
        }
        if (fromCard.getBalance().compareTo(request.amount()) < 0) {
            throw new IllegalArgumentException("Недостаточно средств для перевода");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(request.amount()));
        toCard.setBalance(toCard.getBalance().add(request.amount()));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);
    }
}
