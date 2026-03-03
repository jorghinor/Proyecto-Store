package com.gutti.store.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Ivan Alban & Jorge Quispe
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // --- CONSULTAS CORREGIDAS PARA USAR LA RELACIÓN DE OBJETO ---

    @Query("SELECT c FROM Category c WHERE c.organization.id = :organizationId AND c.deleted = false ORDER BY c.label ASC")
    List<Category> findByOrganizationIdAndDeletedIsFalseOrderByLabelAsc(@Param("organizationId") UUID organizationId);

    @Query("SELECT c FROM Category c WHERE c.id = :id AND c.organization.id = :organizationId AND c.deleted = false")
    Optional<Category> findByIdAndOrganizationIdAndDeletedIsFalse(@Param("id") Long id, @Param("organizationId") UUID organizationId);

    @Query("SELECT c FROM Category c WHERE c.label = :label AND c.organization.id = :organizationId AND c.deleted = false")
    Optional<Category> findByLabelAndOrganizationIdAndDeletedIsFalse(@Param("label") String label, @Param("organizationId") UUID organizationId);

    @Query("SELECT c FROM Category c WHERE c.organization.id = :organizationId AND lower(c.label) LIKE lower(concat('%', :label, '%')) AND c.deleted = false")
    Page<Category> findByOrganizationIdAndLabelContainingIgnoreCaseAndDeletedIsFalse(@Param("organizationId") UUID organizationId, @Param("label") String label, Pageable pageable);

    @Query("SELECT count(c) FROM Category c WHERE c.organization.id = :organizationId AND lower(c.label) LIKE lower(concat('%', :label, '%')) AND c.deleted = false")
    long countByOrganizationIdAndLabelContainingIgnoreCaseAndDeletedIsFalse(@Param("organizationId") UUID organizationId, @Param("label") String label);

    @Query("SELECT c FROM Category c WHERE c.organization.id = :organizationId AND c.deleted = false")
    Page<Category> findByOrganizationIdAndDeletedIsFalse(@Param("organizationId") UUID organizationId, Pageable pageable);

    @Query("SELECT count(c) FROM Category c WHERE c.organization.id = :organizationId AND c.deleted = false")
    long countByOrganizationIdAndDeletedIsFalse(@Param("organizationId") UUID organizationId);

    // --- MÉTODOS ANTIGUOS QUE PUEDEN SEGUIR SIENDO ÚTILES ---
    Optional<Category> findByLabelAndCategoryType(String label, CategoryType categoryType);

    Optional<Category> findByLabelIgnoreCase(String label);
    Optional<Category> findByIdAndDeletedIsFalse(Long id);
}