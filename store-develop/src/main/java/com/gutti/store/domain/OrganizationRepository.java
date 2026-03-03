package com.gutti.store.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    /**
     * Busca todas las organizaciones que pertenecen a un tenant específico.
     * En este caso, como Organization es la entidad principal, simplemente
     * devolvemos la organización que coincide con el ID.
     */
    @Query("select o from Organization o where o.id = :organizationId")
    List<Organization> findByOrganizationId(@Param("organizationId") UUID organizationId);

    /**
     * Busca organizaciones por nombre dentro de un tenant específico.
     */
    @Query("select o from Organization o where o.id = :organizationId and lower(o.name) like lower(concat('%', :searchTerm, '%'))")
    List<Organization> searchByOrganization(@Param("organizationId") UUID organizationId, @Param("searchTerm") String searchTerm);

    // Método para búsqueda global de administrador (sin paginación)
    @Query("select o from Organization o where lower(o.name) like lower(concat('%', :searchTerm, '%'))")
    List<Organization> searchAll(@Param("searchTerm") String searchTerm);

    // --- MÉTODOS PARA PAGINACIÓN ---
    Page<Organization> findByNameContainingIgnoreCase(String name, Pageable pageable);
    long countByNameContainingIgnoreCase(String name);
}