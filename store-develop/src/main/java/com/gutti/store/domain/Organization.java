package com.gutti.store.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;
import java.util.UUID;

@Data
@Entity
@Table(name = "organizations")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    // --- LA SOLUCIÓN ESTÁ AQUÍ ---
    // Este campo le permite a Hibernate gestionar el estado de la entidad (nueva vs. existente)
    @Version
    private Integer version;

    private String name;

    @Column(name = "logo_url")
    private String logoUrl;

    private String phone;

    private String email;

    private String address;
}