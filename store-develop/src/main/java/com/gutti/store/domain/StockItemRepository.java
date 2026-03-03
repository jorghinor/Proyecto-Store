package com.gutti.store.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockItemRepository extends JpaRepository<StockItem, Integer> {

    Optional<StockItem> findByIdAndOrganizationId(Integer id, UUID organizationId);

    void deleteByIdAndOrganizationId(Integer id, UUID organizationId);

    // --- MÉTODOS CORREGIDOS PARA COINCIDIR CON StockService ---

    // 1. Método para buscar por organización (paginado)
    Page<StockItem> findByOrganizationId(UUID organizationId, Pageable pageable);

    // 2. Método para buscar con filtro (renombrado de 'searchByFilter' a 'search')
    @Query("SELECT si FROM StockItem si " +
            "LEFT JOIN FETCH si.product p " +
            "LEFT JOIN FETCH si.productCategory pc " +
            "LEFT JOIN FETCH pc.category c " +
            "WHERE si.organizationId = :organizationId AND " +
            "lower(p.name) LIKE lower(concat('%', :filterText, '%'))")
    Page<StockItem> search(@Param("organizationId") UUID organizationId, @Param("filterText") String filterText, Pageable pageable);

    // 3. Método para contar por organización
    long countByOrganizationId(UUID organizationId);

    // 4. Método para contar con filtro (renombrado de 'countByFilter' a 'countSearch')
    @Query("SELECT count(si) FROM StockItem si " +
            "JOIN si.product p " +
            "WHERE si.organizationId = :organizationId AND " +
            "lower(p.name) LIKE lower(concat('%', :filterText, '%'))")
    long countSearch(@Param("organizationId") UUID organizationId, @Param("filterText") String filterText);
}
