package com.petmatch.controller;

import com.petmatch.dto.AdoptionRequestDTO;
import com.petmatch.dto.AdoptionResponseDTO;
import com.petmatch.model.AdoptionStatus;
import com.petmatch.service.AdoptionRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/adoptions")
@RequiredArgsConstructor
public class AdoptionRequestController {

    private final AdoptionRequestService adoptionService;

    @PostMapping
    public ResponseEntity<AdoptionResponseDTO> createRequest(@RequestBody @Valid AdoptionRequestDTO adoptionRequestDTO) {
        AdoptionResponseDTO created = adoptionService.createAdoptionRequest(adoptionRequestDTO);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/mine")
    public ResponseEntity<List<AdoptionResponseDTO>> getUserRequests() {
        return ResponseEntity.ok(adoptionService.getUserAdoptionRequests());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AdoptionResponseDTO> updateStatus(@PathVariable UUID id,
                                                            @RequestParam AdoptionStatus status) {
        return ResponseEntity.ok(adoptionService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable UUID id) {
        adoptionService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/received")
    public ResponseEntity<List<AdoptionResponseDTO>> getReceivedRequests() {
        List<AdoptionResponseDTO> receivedRequests = adoptionService.getAdoptionRequestsForMyPets();
        return ResponseEntity.ok(receivedRequests);
    }

    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<AdoptionResponseDTO>> getRequestsByPetId(@PathVariable UUID petId) {
        List<AdoptionResponseDTO> requests = adoptionService.getRequestsByPetId(petId);
        return ResponseEntity.ok(requests);
    }

}
