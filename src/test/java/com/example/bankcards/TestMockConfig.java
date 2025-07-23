package com.example.bankcards;

import com.example.bankcards.controller.UserController;
import com.example.bankcards.service.TransferService;
import com.example.bankcards.service.card.CardService;
import com.example.bankcards.service.user.UserService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestMockConfig {

    @Bean
    public CardService cardService() {
        return Mockito.mock(CardService.class);
    }

    @Bean
    public TransferService transferService() {
        return Mockito.mock(TransferService.class);
    }

    @Bean
    public UserService userService() {
        return Mockito.mock(UserService.class);
    }

    @Bean
    public UserController userController() {
        return Mockito.mock(UserController.class);
    }
}