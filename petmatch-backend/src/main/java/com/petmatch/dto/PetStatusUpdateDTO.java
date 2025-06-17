package com.petmatch.dto;

import com.petmatch.model.PetStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PetStatusUpdateDTO {

    @NotNull
    private PetStatus status;

}
