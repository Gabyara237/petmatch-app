package com.petmatch.service;

import java.util.UUID;
import com.petmatch.dto.UserRequestDTO;
import com.petmatch.dto.UserResponseDTO;
import com.petmatch.model.Role;
import com.petmatch.model.User;
import com.petmatch.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UserRequestDTO userRequestDTO;

    @BeforeEach
    void setUp() {
        userRequestDTO = new UserRequestDTO();
        userRequestDTO.setName("Gabriela");
        userRequestDTO.setEmail("gaby@example.com");
        userRequestDTO.setPassword("secret123");
        userRequestDTO.setRole(Role.USER);
    }

    @Test
    void shouldCreateUserSuccessfully() {

        when(userRepository.findByEmail("gaby@example.com")).thenReturn(Optional.empty());

        User fakeSavedUser = User.builder()
                .id(UUID.randomUUID())
                .name("Gabriela")
                .email("gaby@example.com")
                .password(new BCryptPasswordEncoder().encode("secret123"))
                .role(Role.USER)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(fakeSavedUser);

        UserResponseDTO response = userService.createUser(userRequestDTO);

        assertNotNull(response);
        assertEquals("Gabriela", response.getName());
        assertEquals("gaby@example.com", response.getEmail());
        assertEquals(Role.USER, response.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(userRepository.findByEmail("gaby@example.com")).thenReturn(Optional.of(new User()));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.createUser(userRequestDTO));

        assertEquals("Email already in use", exception.getMessage());
        verify(userRepository, never()).save(any());
    }
}
