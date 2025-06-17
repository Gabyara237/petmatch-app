package com.petmatch.dto;

import com.petmatch.model.PetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PetRequestDTO {

    @NotBlank
    private String name;

    @NotNull
    private PetType type;

    @NotBlank
    private String breed;

    @NotNull
    private Integer age;

    @NotBlank
    private String gender;

    private  String description;

}
