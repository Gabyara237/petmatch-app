package com.petmatch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AdoptionRequestDTO {

    @NotNull
    private UUID petId;

    private String message;
}
