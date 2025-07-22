package com.example.bankcards.repository.card;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByCardNumber(String cardNumber);

    Page<Card> findByUserId(Long userId, Pageable pageable);

    @Transactional
    @Modifying
    @Query("DELETE FROM Card c WHERE c.user = :user")
    void deleteAllByUser(@Param("user") User user);

    @Transactional
    @Modifying
    @Query("delete from Card c where c.id=:id")
    int delete(@Param("id") Long id);

}
