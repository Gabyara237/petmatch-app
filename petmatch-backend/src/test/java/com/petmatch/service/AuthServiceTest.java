package com.petmatch.service;

import com.petmatch.dto.LoginRequestDTO;
import com.petmatch.model.Role;
import com.petmatch.model.User;
import com.petmatch.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private LoginRequestDTO loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("gaby@example.com");
        loginRequest.setPassword("secret123");

        user = User.builder()
                .id(UUID.randomUUID())
                .email("gaby@example.com")
                .password("hashedPassword")
                .role(Role.USER)
                .build();
    }

    @Test
    void shouldAuthenticateUserSuccessfully() {
        when(userRepository.findByEmail("gaby@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret123", "hashedPassword")).thenReturn(true);

        User result = authService.authenticate(loginRequest);

        assertNotNull(result);
        assertEquals("gaby@example.com", result.getEmail());
        verify(userRepository, times(1)).findByEmail("gaby@example.com");
        verify(passwordEncoder, times(1)).matches("secret123", "hashedPassword");
    }

    @Test
    void shouldThrowExceptionWhenEmailNotFound() {
        when(userRepository.findByEmail("gaby@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authService.authenticate(loginRequest));

        assertEquals("Email not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("gaby@example.com");
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsInvalid() {
        when(userRepository.findByEmail("gaby@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret123", "hashedPassword")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authService.authenticate(loginRequest));

        assertEquals("Invalid password", exception.getMessage());
        verify(passwordEncoder, times(1)).matches("secret123", "hashedPassword");
    }
}
