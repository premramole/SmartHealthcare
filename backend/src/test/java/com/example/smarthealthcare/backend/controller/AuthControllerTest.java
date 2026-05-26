package com.example.smarthealthcare.backend.controller;

import com.example.smarthealthcare.backend.dto.UserRegistrationDto;
import com.example.smarthealthcare.backend.model.User;
import com.example.smarthealthcare.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenRegisterUserWithNewCredentials_thenReturnCreated() throws Exception {
        // Given
        UserRegistrationDto registrationDto = new UserRegistrationDto(
            "user123", "John Doe", "john@example.com", "patient");
        
        User user = new User("user123", "John Doe", "john@example.com", "patient");
        user.setId(1L);
        
        when(userService.existsByUserIdOrEmail("user123", "john@example.com")).thenReturn(false);
        when(userService.saveUser(org.mockito.ArgumentMatchers.any(User.class))).thenReturn(user);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void whenRegisterUserWithExistingCredentials_thenReturnConflict() throws Exception {
        // Given
        UserRegistrationDto registrationDto = new UserRegistrationDto(
            "user123", "John Doe", "john@example.com", "patient");
        
        when(userService.existsByUserIdOrEmail("user123", "john@example.com")).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isConflict())
                .andExpect(content().string("User with this ID or email already exists"));
    }
}