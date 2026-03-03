package com.gutti.store.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ItemPropertyRepository extends JpaRepository<ItemProperty, Integer> {

    List<ItemProperty> findByOrganizationId(UUID organizationId);

    // --- MÉTODOS PARA PAGINACIÓN Y FILTRADO (CORREGIDOS) ---
    Page<ItemProperty> findByOrganizationIdAndLabelContainingIgnoreCase(UUID organizationId, String label, Pageable pageable);

    long countByOrganizationIdAndLabelContainingIgnoreCase(UUID organizationId, String label);

    Page<ItemProperty> findByOrganizationId(UUID organizationId, Pageable pageable);

    long countByOrganizationId(UUID organizationId);
}