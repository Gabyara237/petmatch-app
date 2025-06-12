package com.petmatch.dto;


import com.petmatch.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequestDTO {
    @NotBlank
    private  String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private  String password;

    private Role role;

}
