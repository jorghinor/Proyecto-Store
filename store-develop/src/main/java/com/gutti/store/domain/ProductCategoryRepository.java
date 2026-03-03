package com.gutti.store.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    // --- CONSULTAS CORREGIDAS PARA USAR LA RELACIÓN DE OBJETO ---

    @Query("SELECT count(pc) > 0 FROM ProductCategory pc WHERE pc.product.id = :productId AND pc.organizationId = :organizationId")
    boolean existsByProductId(@Param("productId") Long productId, @Param("organizationId") UUID organizationId);

    @Query("SELECT count(pc) > 0 FROM ProductCategory pc WHERE pc.category.id = :categoryId AND pc.organizationId = :organizationId")
    boolean existsByCategoryId(@Param("categoryId") Long categoryId, @Param("organizationId") UUID organizationId);

    @Query("SELECT pc FROM ProductCategory pc WHERE pc.product.id = :productId AND pc.category.id = :categoryId AND pc.organizationId = :organizationId")
    Optional<ProductCategory> findByProductIdAndCategoryId(@Param("productId") Long productId, @Param("categoryId") Long categoryId, @Param("organizationId") UUID organizationId);

    List<ProductCategory> findAllByOrganizationId(UUID organizationId);

    @Query("SELECT pc FROM ProductCategory pc WHERE pc.product.id = :productId AND pc.organizationId = :organizationId")
    List<ProductCategory> findAllByProductId(@Param("productId") Long productId, @Param("organizationId") UUID organizationId);
}