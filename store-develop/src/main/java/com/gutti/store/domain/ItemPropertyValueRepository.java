package com.gutti.store.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ItemPropertyValueRepository extends JpaRepository<ItemPropertyValue, Integer> {

    @Query("SELECT ipv FROM ItemPropertyValue ipv WHERE ipv.itemProperty.id = :propertyId AND ipv.organizationId = :organizationId")
    List<ItemPropertyValue> findByPropertyIdAndOrganizationId(@Param("propertyId") Integer propertyId, @Param("organizationId") UUID organizationId);

    boolean existsByItemProperty(ItemProperty itemProperty);

    // --- MÉTODOS PARA PAGINACIÓN Y FILTRADO (CORREGIDOS) ---
    Page<ItemPropertyValue> findByOrganizationIdAndValueContainingIgnoreCase(UUID organizationId, String value, Pageable pageable);

    long countByOrganizationIdAndValueContainingIgnoreCase(UUID organizationId, String value);

    Page<ItemPropertyValue> findByOrganizationId(UUID organizationId, Pageable pageable);

    long countByOrganizationId(UUID organizationId);
}
