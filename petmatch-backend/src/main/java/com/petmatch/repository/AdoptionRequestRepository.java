package com.petmatch.repository;

import com.petmatch.model.AdoptionRequest;
import com.petmatch.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AdoptionRequestRepository extends JpaRepository<AdoptionRequest, UUID> {
    List<AdoptionRequest> findByApplicant(User applicant);

    List<AdoptionRequest> findByPetOwner(User owner);

}
