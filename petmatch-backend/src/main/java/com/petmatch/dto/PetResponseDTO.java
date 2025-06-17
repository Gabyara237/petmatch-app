package com.petmatch.dto;

import com.petmatch.model.PetType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PetResponseDTO {

    private UUID id;
    private String name;
    private PetType type;
    private String breed;
    private String gender;
    private Integer age;
    private String description;

}
