package com.petmatch.controller;

import com.petmatch.dto.PetRequestDTO;
import com.petmatch.dto.PetResponseDTO;
import com.petmatch.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.DeclareError;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;


    @PostMapping
    public ResponseEntity<PetResponseDTO> createPet(@RequestBody @Valid PetRequestDTO petRequestDTO) {
        PetResponseDTO createdPet = petService.createPet(petRequestDTO);
        return ResponseEntity.status(201).body(createdPet);
    }

    @GetMapping
    public ResponseEntity<List<PetResponseDTO>> getAllPets(){
        List<PetResponseDTO> pets = petService.getAllPets();
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetResponseDTO> getPetById(@PathVariable UUID id){
        PetResponseDTO pet = petService.getPetById(id);
        return ResponseEntity.ok(pet);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetResponseDTO> updatePet( @PathVariable UUID id, @RequestBody @Valid PetRequestDTO petRequestDTO){
        PetResponseDTO updatePet = petService.updatePet(id,petRequestDTO);
        return ResponseEntity.ok(updatePet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable UUID id){
        petService.deletePet(id);

        return ResponseEntity.noContent().build();
    }

}
