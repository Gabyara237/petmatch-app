package com.petmatch.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pet {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private  String species;

    private String breed;

    private Integer age;

    private String size;

    private String description;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
    }

}
