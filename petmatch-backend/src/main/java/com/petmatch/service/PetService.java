package com.petmatch.service;

import com.petmatch.dto.PetRequestDTO;
import com.petmatch.dto.PetResponseDTO;
import com.petmatch.model.Pet;
import com.petmatch.repository.PetRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;

    public PetResponseDTO createPet(PetRequestDTO petRequestDTO){
        Pet pet = Pet.builder()
                .name(petRequestDTO.getName())
                .age(petRequestDTO.getAge())
                .type(petRequestDTO.getType())
                .gender(petRequestDTO.getGender())
                .status("AVAILABLE")
                .description(petRequestDTO.getDescription())
                .build();

        Pet savedPet = petRepository.save(pet);
        return mapToResponse(savedPet);
    }

    public List<PetResponseDTO> getAllPets(){
        return petRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PetResponseDTO getPetById(UUID id){

        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found"));
        return mapToResponse(pet);

    }

    public PetResponseDTO updatePet(UUID id, PetRequestDTO petRequestDTO){
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found"));

        pet.setName(petRequestDTO.getName());
        pet.setAge(petRequestDTO.getAge());
        pet.setType(petRequestDTO.getType());
        pet.setDescription(petRequestDTO.getDescription());

        return mapToResponse(petRepository.save(pet));

    }
    public void deletePet(UUID id){
        if(!petRepository.existsById(id)){
            throw  new EntityNotFoundException("Pet noy found");
        }
        petRepository.deleteById(id);
    }

    private PetResponseDTO mapToResponse(Pet pet) {
        return PetResponseDTO.builder()
                .id(pet.getId())
                .name(pet.getName())
                .age(pet.getAge())
                .type(pet.getType())
                .breed(pet.getBreed())
                .gender(pet.getGender())
                .description(pet.getDescription())
                .build();
    }

}
