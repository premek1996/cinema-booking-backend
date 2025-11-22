package com.example.cinemabooking.common.jpa;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@MappedSuperclass
@EqualsAndHashCode(of = "uuid")
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "uuid", nullable = false, unique = true, updatable = false)
    @Builder.Default
    protected UUID uuid = UUID.randomUUID();

}
