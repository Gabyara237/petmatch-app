package com.petmatch.dto;

import com.petmatch.model.AdoptionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AdoptionResponseDTO {

    private UUID id;
    private UUID petId;
    private String petName;

    private UUID applicantId;
    private String applicantName;

    private String message;
    private AdoptionStatus status;
    private LocalDateTime createdAt;
}
