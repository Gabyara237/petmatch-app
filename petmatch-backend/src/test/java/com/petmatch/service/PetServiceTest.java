package com.petmatch.service;

import com.petmatch.dto.PetRequestDTO;
import com.petmatch.dto.PetResponseDTO;
import com.petmatch.model.*;
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
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PetService petService;

    private Pet pet;

    @BeforeEach
    void setUp() {
        pet = Pet.builder()
                .id(UUID.randomUUID())
                .name("Lobby")
                .age(3)
                .type(PetType.DOG)
                .breed("Labrador")
                .gender("Male")
                .description("Very friendly and affectionate")
                .status(PetStatus.AVAILABLE)
                .build();
    }

    @Test
    void shouldReturnPetWhenGetPetById() {
        when(petRepository.findById(pet.getId())).thenReturn(Optional.of(pet));

        PetResponseDTO result = petService.getPetById(pet.getId());

        assertNotNull(result);
        assertEquals("Lobby", result.getName());
        assertEquals(PetType.DOG, result.getType());
        verify(petRepository, times(1)).findById(pet.getId());
    }

    @Test
    void shouldThrowExceptionWhenPetNotFound() {
        UUID fakeId = UUID.randomUUID();
        when(petRepository.findById(fakeId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                petService.getPetById(fakeId));

        verify(petRepository, times(1)).findById(fakeId);
    }

    @Test
    void shouldReturnAllPets() {
        Pet pet2 = Pet.builder()
                .id(UUID.randomUUID())
                .name("Simba")
                .age(2)
                .type(PetType.DOG)
                .breed("Golden retriever")
                .gender("Male")
                .description("Very playful and friendly with children.")
                .status(PetStatus.AVAILABLE)
                .build();

        when(petRepository.findAll()).thenReturn(List.of(pet, pet2));

        var result = petService.getAllPets();

        assertEquals(2, result.size());
        assertEquals("Lobby", result.get(0).getName());
        assertEquals("Simba", result.get(1).getName());
        verify(petRepository, times(1)).findAll();
    }

    @Test
    void shouldUpdatePetStatusSuccessfully() {
        UUID id = pet.getId();
        when(petRepository.findById(id)).thenReturn(Optional.of(pet));
        when(petRepository.save(any(Pet.class))).thenReturn(pet);

        PetResponseDTO result = petService.updatePetStatus(id, PetStatus.ADOPTED);

        assertEquals(PetStatus.ADOPTED, pet.getStatus());
        assertEquals("Lobby", result.getName());
        verify(petRepository, times(1)).findById(id);
        verify(petRepository, times(1)).save(pet);
    }

    @Test
    void shouldDeletePetSuccessfully() {
        UUID id = pet.getId();
        when(petRepository.existsById(id)).thenReturn(true);

        petService.deletePet(id);

        verify(petRepository, times(1)).deleteById(id);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonexistentPet() {
        UUID id = UUID.randomUUID();
        when(petRepository.existsById(id)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
                petService.deletePet(id));

        verify(petRepository, never()).deleteById(any());
    }


    @Test
    void shouldReturnPetsByUserId() {
        UUID userId = UUID.randomUUID();
        pet.setOwner(User.builder().id(userId).build());

        when(petRepository.findByOwnerId(userId)).thenReturn(List.of(pet));

        var result = petService.getPetsByUserId(userId);

        assertEquals(1, result.size());
        assertEquals("Lobby", result.get(0).getName());
        verify(petRepository, times(1)).findByOwnerId(userId);
    }

    @Test
    void shouldCreatePetWithAuthenticatedUser() {

        PetRequestDTO requestDTO = new PetRequestDTO();
        requestDTO.setName("Cookie");
        requestDTO.setAge(4);
        requestDTO.setType(PetType.DOG);
        requestDTO.setBreed("Golden Retriever");
        requestDTO.setGender("Male");
        requestDTO.setDescription("Very playful and friendly");


        User user = User.builder()
                .id(UUID.randomUUID())
                .email("gaby@example.com")
                .build();

        UserDetails userDetails = mock(UserDetails.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);


        when(userDetails.getUsername()).thenReturn("gaby@example.com");
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);


        when(userRepository.findByEmail("gaby@example.com")).thenReturn(Optional.of(user));
        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> {
            Pet saved = invocation.getArgument(0);
            saved.setId(UUID.randomUUID());
            return saved;
        });


        PetResponseDTO response = petService.createPet(requestDTO);


        assertNotNull(response);
        assertEquals("Cookie", response.getName());
        verify(petRepository, times(1)).save(any(Pet.class));
        verify(userRepository, times(1)).findByEmail("gaby@example.com");
    }

    @Test
    void shouldThrowExceptionIfUserNotAuthenticated() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        PetRequestDTO requestDTO = new PetRequestDTO();
        requestDTO.setName("Ghost");

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                petService.createPet(requestDTO));

        assertEquals("Unauthenticated user", exception.getMessage());
    }

}

