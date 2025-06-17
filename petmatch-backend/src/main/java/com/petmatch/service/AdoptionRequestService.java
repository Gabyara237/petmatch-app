package com.petmatch.service;

import com.petmatch.dto.AdoptionRequestDTO;
import com.petmatch.dto.AdoptionResponseDTO;
import com.petmatch.model.*;
import com.petmatch.repository.AdoptionRequestRepository;
import com.petmatch.repository.PetRepository;
import com.petmatch.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdoptionRequestService {

    private final AdoptionRequestRepository adoptionRequestRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;


    public AdoptionResponseDTO createAdoptionRequest(AdoptionRequestDTO adoptionRequestDTO) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Pet pet = petRepository.findById(adoptionRequestDTO.getPetId())
                .orElseThrow(() -> new EntityNotFoundException("Pet not found"));


        if (adoptionRequestRepository.existsByApplicantAndPet(user, pet)) {
            throw new RuntimeException("You have already requested to adopt this pet.");
        }


        AdoptionRequest request = AdoptionRequest.builder()
                .applicant(user)
                .pet(pet)
                .message(adoptionRequestDTO.getMessage())
                .status(AdoptionStatus.PENDING)
                .build();

        return mapToResponseDTO(adoptionRequestRepository.save(request));
    }

    public List<AdoptionResponseDTO> getUserAdoptionRequests() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return adoptionRequestRepository.findByApplicant(user)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    public AdoptionResponseDTO updateStatus(UUID requestId, AdoptionStatus status) {
        AdoptionRequest request = adoptionRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        request.setStatus(status);
        return mapToResponseDTO(adoptionRequestRepository.save(request));
    }

    public void deleteRequest(UUID requestId) {
        if (!adoptionRequestRepository.existsById(requestId)) {
            throw new EntityNotFoundException("Request not found");
        }
        adoptionRequestRepository.deleteById(requestId);
    }

    public List<AdoptionResponseDTO> getAdoptionRequestsForMyPets() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<AdoptionRequest> requests = adoptionRequestRepository.findByPetOwner(owner);

        return requests.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    public AdoptionResponseDTO approveRequest(UUID requestId) {
        AdoptionRequest request = adoptionRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        if (request.getStatus() != AdoptionStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be approved");
        }

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        User owner = request.getPet().getOwner();

        if (owner == null || !owner.getEmail().equals(userEmail)) {
            throw new AccessDeniedException("You are not the owner of this pet");
        }

        request.setStatus(AdoptionStatus.APPROVED);
        adoptionRequestRepository.save(request);

        Pet pet = request.getPet();
        pet.setStatus(PetStatus.ADOPTED);
        petRepository.save(pet);

        List<AdoptionRequest> otherRequests = adoptionRequestRepository.findByPetAndIdNot(pet, request.getId());
        for (AdoptionRequest other : otherRequests) {
            if (other.getStatus() == AdoptionStatus.PENDING) {
                other.setStatus(AdoptionStatus.REJECTED);
            }
        }
        adoptionRequestRepository.saveAll(otherRequests);

        return mapToResponseDTO(request);
    }



    private AdoptionResponseDTO mapToResponseDTO(AdoptionRequest request) {
        return AdoptionResponseDTO.builder()
                .id(request.getId())
                .petName(request.getPet().getName())
                .petId(request.getPet().getId())
                .applicantName(request.getApplicant().getName())
                .message(request.getMessage())
                .status(request.getStatus())
                .applicantId(request.getApplicant().getId())
                .createdAt(request.getCreatedAt())
                .build();
    }

    public List<AdoptionResponseDTO> getRequestsByPetId(UUID petId) {
        List<AdoptionRequest> requests = adoptionRequestRepository.findByPetId(petId);
        return requests.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

}
