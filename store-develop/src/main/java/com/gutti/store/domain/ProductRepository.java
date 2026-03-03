package com.gutti.store.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // --- CONSULTAS CORREGIDAS PARA USAR LA RELACIÓN DE OBJETO ---

    @Query("SELECT p FROM Product p WHERE p.organization.id = :organizationId AND p.deleted = false")
    List<Product> findByOrganizationIdAndDeletedIsFalse(@Param("organizationId") UUID organizationId);

    // --- ESTA ES LA CONSULTA CORREGIDA ---
    // Añadimos LEFT JOIN FETCH p.organization para que siempre cargue los datos de la empresa.
    @Query(value = "SELECT p FROM Product p LEFT JOIN FETCH p.organization WHERE p.organization.id = :organizationId AND p.deleted = false",
            countQuery = "SELECT count(p) FROM Product p WHERE p.organization.id = :organizationId AND p.deleted = false")
    Page<Product> findByOrganizationIdAndDeletedIsFalse(@Param("organizationId") UUID organizationId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.organization.id = :organizationId AND lower(p.name) LIKE lower(concat('%', :name, '%')) AND p.deleted = false")
    Page<Product> findByOrganizationIdAndNameContainingIgnoreCaseAndDeletedIsFalse(@Param("organizationId") UUID organizationId, @Param("name") String name, Pageable pageable);

    @Query("SELECT count(p) FROM Product p WHERE p.organization.id = :organizationId AND p.deleted = false")
    long countByOrganizationIdAndDeletedIsFalse(@Param("organizationId") UUID organizationId);

    @Query("SELECT count(p) FROM Product p WHERE p.organization.id = :organizationId AND lower(p.name) LIKE lower(concat('%', :name, '%')) AND p.deleted = false")
    long countByOrganizationIdAndNameContainingIgnoreCaseAndDeletedIsFalse(@Param("organizationId") UUID organizationId, @Param("name") String name);

    // --- MÉTODO PARA EL CATÁLOGO ---

    @Query(value = "SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.organization " +
            "LEFT JOIN p.categories pc " +
            "LEFT JOIN pc.category c " +
            "WHERE p.organization.id = :organizationId AND p.deleted = false " +
            "AND (lower(p.name) LIKE lower(concat('%', :filterText, '%')) " +
            "OR lower(c.label) LIKE lower(concat('%', :filterText, '%')))",
            countQuery = "SELECT count(DISTINCT p) FROM Product p " +
                    "LEFT JOIN p.categories pc " +
                    "LEFT JOIN pc.category c " +
                    "WHERE p.organization.id = :organizationId AND p.deleted = false " +
                    "AND (lower(p.name) LIKE lower(concat('%', :filterText, '%')) " +
                    "OR lower(c.label) LIKE lower(concat('%', :filterText, '%')))")
    Page<Product> findByOrganizationIdAndFilterText(@Param("organizationId") UUID organizationId, @Param("filterText") String filterText, Pageable pageable);

    // --- MÉTODO DE CONTEO PARA EL CATÁLOGO ---
    @Query("SELECT count(DISTINCT p) FROM Product p " +
            "LEFT JOIN p.categories pc " +
            "LEFT JOIN pc.category c " +
            "WHERE p.organization.id = :organizationId AND p.deleted = false " +
            "AND (lower(p.name) LIKE lower(concat('%', :filterText, '%')) " +
            "OR lower(c.label) LIKE lower(concat('%', :filterText, '%')))")
    long countByOrganizationIdAndFilterText(@Param("organizationId") UUID organizationId, @Param("filterText") String filterText);

    // --- MÉTODOS ANTIGUOS QUE PUEDEN SEGUIR SIENDO ÚTILES ---
    Optional<Product> findByNameIgnoreCase(String name);

    Optional<Product> findByIdAndDeletedIsFalse(Long id);
}
