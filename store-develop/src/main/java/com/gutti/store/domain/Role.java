package com.gutti.store.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "roles")
public class Role {

    @Id
    // CORREGIDO: El ID es un String y no se autogenera.
    private String id;

    @Column(unique = true, nullable = false)
    private String name;
}
