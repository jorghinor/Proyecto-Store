package com.gutti.store.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockItemPropertyRepository extends JpaRepository<StockItemProperty, Integer> {

    List<StockItemProperty> findAllByOrganizationId(UUID organizationId);

    Optional<StockItemProperty> findByIdAndOrganizationId(Integer id, UUID organizationId);

    void deleteByIdAndOrganizationId(Integer id, UUID organizationId);

    boolean existsByItemPropertyValue(ItemPropertyValue itemPropertyValue);
}
