package com.petmatch.service;

import com.petmatch.dto.AdoptionRequestDTO;
import com.petmatch.dto.AdoptionResponseDTO;
import com.petmatch.model.*;
import com.petmatch.repository.AdoptionRequestRepository;
import com.petmatch.repository.PetRepository;
import com.petmatch.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdoptionRequestServiceTest {

    @Mock
    private AdoptionRequestRepository adoptionRequestRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdoptionRequestService adoptionRequestService;

    private AdoptionRequest request;
    private Pet pet;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .name("Gabriela")
                .email("gaby@example.com")
                .build();

        pet = Pet.builder()
                .id(UUID.randomUUID())
                .name("Lobby")
                .owner(user)
                .build();

        request = AdoptionRequest.builder()
                .id(UUID.randomUUID())
                .applicant(user)
                .pet(pet)
                .status(AdoptionStatus.PENDING)
                .message("I want to adopt Lobby.")
                .build();
    }

    @Test
    void shouldUpdateStatusSuccessfully() {
        when(adoptionRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));
        when(adoptionRequestRepository.save(any())).thenReturn(request);

        AdoptionResponseDTO result = adoptionRequestService.updateStatus(request.getId(), AdoptionStatus.APPROVED);

        assertNotNull(result);
        assertEquals(AdoptionStatus.APPROVED, result.getStatus());
        verify(adoptionRequestRepository, times(1)).findById(request.getId());
        verify(adoptionRequestRepository, times(1)).save(request);
    }

    @Test
    void shouldDeleteRequestSuccessfully() {
        when(adoptionRequestRepository.existsById(request.getId())).thenReturn(true);

        adoptionRequestService.deleteRequest(request.getId());

        verify(adoptionRequestRepository, times(1)).deleteById(request.getId());
    }


    @Test
    void shouldThrowWhenDeletingNonExistentRequest() {
        UUID fakeId = UUID.randomUUID();
        when(adoptionRequestRepository.existsById(fakeId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
                adoptionRequestService.deleteRequest(fakeId));

        verify(adoptionRequestRepository, never()).deleteById(any());
    }

    @Test
    void shouldReturnRequestsByPetId() {
        when(adoptionRequestRepository.findByPetId(pet.getId())).thenReturn(List.of(request));

        List<AdoptionResponseDTO> result = adoptionRequestService.getRequestsByPetId(pet.getId());

        assertEquals(1, result.size());
        assertEquals("Lobby", result.get(0).getPetName());
        verify(adoptionRequestRepository, times(1)).findByPetId(pet.getId());
    }

    private void mockAuthenticatedUser(String email) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(authentication.getName()).thenReturn(email);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void shouldCreateAdoptionRequestSuccessfully() {
        mockAuthenticatedUser("gaby@example.com");

        AdoptionRequestDTO dto = new AdoptionRequestDTO();
        dto.setPetId(pet.getId());
        dto.setMessage("I would love to adopt him.");

        when(userRepository.findByEmail("gaby@example.com")).thenReturn(Optional.of(user));
        when(petRepository.findById(pet.getId())).thenReturn(Optional.of(pet));
        when(adoptionRequestRepository.existsByApplicantAndPet(user, pet)).thenReturn(false);
        when(adoptionRequestRepository.save(any())).thenReturn(request);

        AdoptionResponseDTO result = adoptionRequestService.createAdoptionRequest(dto);

        assertNotNull(result);
        assertEquals("Lobby", result.getPetName());
        assertEquals("Gabriela", result.getApplicantName());
    }

    @Test
    void shouldReturnUserAdoptionRequests() {
        mockAuthenticatedUser("gaby@example.com");

        when(userRepository.findByEmail("gaby@example.com")).thenReturn(Optional.of(user));
        when(adoptionRequestRepository.findByApplicant(user)).thenReturn(List.of(request));

        List<AdoptionResponseDTO> result = adoptionRequestService.getUserAdoptionRequests();

        assertEquals(1, result.size());
        assertEquals("Lobby", result.get(0).getPetName());
    }

    @Test
    void shouldReturnRequestsForMyPets() {
        mockAuthenticatedUser("gaby@example.com");

        when(userRepository.findByEmail("gaby@example.com")).thenReturn(Optional.of(user));
        when(adoptionRequestRepository.findByPetOwner(user)).thenReturn(List.of(request));

        List<AdoptionResponseDTO> result = adoptionRequestService.getAdoptionRequestsForMyPets();

        assertEquals(1, result.size());
        assertEquals("Lobby", result.get(0).getPetName());
    }

    @Test
    void shouldApproveAdoptionRequestSuccessfully() {
        mockAuthenticatedUser("gaby@example.com");

        request.getPet().setStatus(PetStatus.AVAILABLE);
        request.setStatus(AdoptionStatus.PENDING);

        AdoptionRequest otherRequest = AdoptionRequest.builder()
                .id(UUID.randomUUID())
                .pet(pet)
                .status(AdoptionStatus.PENDING)
                .build();

        when(adoptionRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));
        when(adoptionRequestRepository.findByPetAndIdNot(pet, request.getId())).thenReturn(List.of(otherRequest));
        when(adoptionRequestRepository.save(any())).thenReturn(request);
        when(petRepository.save(any())).thenReturn(pet);

        AdoptionResponseDTO result = adoptionRequestService.approveRequest(request.getId());

        assertEquals(AdoptionStatus.APPROVED, result.getStatus());
        assertEquals(PetStatus.ADOPTED, pet.getStatus());
        verify(adoptionRequestRepository).saveAll(any());
    }
}
