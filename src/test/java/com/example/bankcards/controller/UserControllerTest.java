package com.example.bankcards.controller;

import com.example.bankcards.entity.User;
import com.example.bankcards.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
    }

    @Test
    void getUserById_UserExists_ReturnsOkWithUser() {
        when(userService.findById(1L)).thenReturn(Optional.of(testUser));
        ResponseEntity<User> response = userController.getUserById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUser, response.getBody());
        verify(userService).findById(1L);
    }

    @Test
    void getUserById_UserNotFound_ReturnsNotFound() {
        when(userService.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<User> response = userController.getUserById(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService).findById(1L);
    }

    @Test
    void updateUser_ValidUser_ReturnsOkWithUpdatedUser() {
        when(userService.update(any(User.class))).thenReturn(true);
        ResponseEntity<User> response = userController.updateUser(testUser);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUser, response.getBody());
        verify(userService).update(testUser);
    }

    @Test
    void updateUser_NullId_ReturnsNotFound() {
        testUser.setId(null);
        ResponseEntity<User> response = userController.updateUser(testUser);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, never()).update(any(User.class));
    }

    @Test
    void updateUser_UpdateFails_ReturnsBadRequest() {
        when(userService.update(any(User.class))).thenReturn(false);
        ResponseEntity<User> response = userController.updateUser(testUser);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService).update(testUser);
    }

    @Test
    void deleteUser_ValidId_ReturnsNoContent() {
        doNothing().when(userService).deleteById(anyLong());
        ResponseEntity<Void> response = userController.deleteUser(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService).deleteById(1L);
    }
}