package com.example.smarthealthcare.backend.controller;

import com.example.smarthealthcare.backend.dto.UserRegistrationDto;
import com.example.smarthealthcare.backend.model.User;
import com.example.smarthealthcare.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000") // Adjust as needed for your frontend
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDto registrationDto) {
        // Check if user already exists
        if (userService.existsByUserIdOrEmail(registrationDto.getUserId(), registrationDto.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("User with this ID or email already exists");
        }
        
        // Create new user
        User user = new User();
        user.setUserId(registrationDto.getUserId());
        user.setName(registrationDto.getName());
        user.setEmail(registrationDto.getEmail());
        user.setRole(registrationDto.getRole());
        
        User savedUser = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginDto loginDto) {
        // Find user by email
        var userOptional = userService.getUserByEmail(loginDto.getEmail());
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // In a real application, you would verify the password here
            // For now, we'll just return the user information
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid email or password");
        }
    }
    
    // Inner class for login DTO
    public static class UserLoginDto {
        private String email;
        private String password;
        
        public UserLoginDto() {
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
    }
}