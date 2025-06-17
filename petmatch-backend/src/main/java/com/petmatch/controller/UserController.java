package com.petmatch.controller;

import com.petmatch.dto.PetResponseDTO;
import com.petmatch.dto.UserRequestDTO;
import com.petmatch.dto.UserResponseDTO;
import com.petmatch.model.User;
import com.petmatch.repository.UserRepository;
import com.petmatch.service.PetService;
import com.petmatch.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PetService petService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody @Valid UserRequestDTO userDto){
        UserResponseDTO response = userService.createUser(userDto);
        return  ResponseEntity.status(201).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .map(user -> ResponseEntity.ok(Map.of(
                        "id", user.getId(),
                        "name", user.getName(),
                        "email", user.getEmail(),
                        "role", user.getRole()
                )))
                .orElse(ResponseEntity.status(404).body(Map.of("error", "User not found")));
    }

    @GetMapping("/me/pets")
    public ResponseEntity<?> getMyPets(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if(principal instanceof User user) {
            List<PetResponseDTO> myPets = petService.getPetsByUserId(user.getId());
            return ResponseEntity.ok(myPets);
        }
        return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
    }
}
