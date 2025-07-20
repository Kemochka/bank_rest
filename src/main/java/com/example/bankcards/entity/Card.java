package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String cardNumber;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private LocalDate expiryDate;
    @Enumerated(EnumType.STRING)
    private StatusCode status;
    @Column(precision = 19, scale = 2)
    private BigDecimal balance;


}
