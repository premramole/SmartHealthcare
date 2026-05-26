package com.example.smarthealthcare.backend.service;

import com.example.smarthealthcare.backend.model.User;
import com.example.smarthealthcare.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenSaveUser_thenUserShouldBeSaved() {
        // Given
        User user = new User("user123", "John Doe", "john@example.com", "patient");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        User savedUser = userService.saveUser(user);

        // Then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUserId()).isEqualTo("user123");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void whenGetUserByUserId_thenReturnUser() {
        // Given
        User user = new User("user123", "John Doe", "john@example.com", "patient");
        when(userRepository.findByUserId("user123")).thenReturn(Optional.of(user));

        // When
        Optional<User> foundUser = userService.getUserByUserId("user123");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("John Doe");
        verify(userRepository, times(1)).findByUserId("user123");
    }

    @Test
    void whenDeleteUser_thenRepositoryDeleteShouldBeCalled() {
        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository, times(1)).deleteById(1L);
    }
}