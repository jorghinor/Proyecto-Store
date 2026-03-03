package com.gutti.store.domain;

import com.gutti.store.dtos.CategorySalesDto;
import com.gutti.store.dtos.TopSellingProductDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockItemDetailRepository extends JpaRepository<StockItemDetail, Integer> {

    List<StockItemDetail> findAllByOrganizationId(UUID organizationId);

    Optional<StockItemDetail> findByIdAndOrganizationId(Integer id, UUID organizationId);

    void deleteByIdAndOrganizationId(Integer id, UUID organizationId);

    @Query("SELECT new com.gutti.store.dtos.CategorySalesDto(c.label, SUM(d.quantity * d.unitPrice)) " +
            "FROM StockItemDetail d " +
            "JOIN d.stockItem si " +
            "JOIN si.productCategory pc " +
            "JOIN pc.category c " +
            "WHERE si.organizationId = :organizationId " +
            "GROUP BY c.label " +
            "ORDER BY SUM(d.quantity * d.unitPrice) DESC")
    List<CategorySalesDto> findTotalSalesPerCategory(@Param("organizationId") UUID organizationId);

    @Query("SELECT SUM(d.quantity * d.unitPrice) FROM StockItemDetail d WHERE d.stockItem.organizationId = :organizationId")
    BigDecimal sumTotalSalesByOrganizationId(@Param("organizationId") UUID organizationId);

    @Query("SELECT SUM(d.quantity) FROM StockItemDetail d WHERE d.stockItem.organizationId = :organizationId")
    Long sumTotalQuantityByOrganizationId(@Param("organizationId") UUID organizationId);

    @Query("SELECT new com.gutti.store.dtos.TopSellingProductDto(p.name, SUM(d.quantity)) " +
            "FROM StockItemDetail d " +
            "JOIN d.stockItem si " +
            "JOIN si.product p " +
            "WHERE si.organizationId = :organizationId " +
            "GROUP BY p.name " +
            "ORDER BY SUM(d.quantity) DESC")
    List<TopSellingProductDto> findTopSellingProducts(@Param("organizationId") UUID organizationId);
}

