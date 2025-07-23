package com.example.bankcards.service.user;

import com.example.bankcards.entity.User;
import com.example.bankcards.repository.card.CardRepository;
import com.example.bankcards.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserRepository userRepository;
    private CardRepository cardRepository;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        cardRepository = mock(CardRepository.class);
        userService = new UserServiceImpl(userRepository, cardRepository);
    }

    @Test
    void save_ShouldCallRepositorySave() {
        User user = new User();
        when(userRepository.save(user)).thenReturn(user);
        User savedUser = userService.save(user);
        assertThat(savedUser).isEqualTo(user);
        verify(userRepository).save(user);
    }

    @Test
    void findById_ShouldReturnUserOptional() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Optional<User> result = userService.findById(1L);
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(user);
        verify(userRepository).findById(1L);
    }

    @Test
    void deleteById_ShouldDeleteUserAndCards_WhenUserExists() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.deleteById(1L);
        verify(cardRepository).deleteAllByUser(user);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteById_ShouldNotDelete_WhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        userService.deleteById(1L);
        verify(cardRepository, never()).deleteAllByUser(any());
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void update_ShouldReturnTrue_WhenUpdateCountGreaterThanZero() {
        User user = new User();
        when(userRepository.update(user)).thenReturn(1);
        boolean result = userService.update(user);
        assertThat(result).isTrue();
        verify(userRepository).update(user);
    }

    @Test
    void update_ShouldReturnFalse_WhenUpdateCountIsZero() {
        User user = new User();
        when(userRepository.update(user)).thenReturn(0);
        boolean result = userService.update(user);
        assertThat(result).isFalse();
        verify(userRepository).update(user);
    }
}