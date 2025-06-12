package com.petmatch.controller;

import com.petmatch.dto.UserRequestDTO;
import com.petmatch.dto.UserResponseDTO;
import com.petmatch.model.User;
import com.petmatch.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody @Valid UserRequestDTO userDto){
        UserResponseDTO response = userService.createUser(userDto);
        return  ResponseEntity.status(201).body(response);
    }

}
