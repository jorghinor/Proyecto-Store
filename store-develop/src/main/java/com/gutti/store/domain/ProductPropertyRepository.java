package com.gutti.store.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductPropertyRepository extends JpaRepository<ProductProperty, Integer> {

    List<ProductProperty> findAllByOrganizationId(UUID organizationId);

    Optional<ProductProperty> findByIdAndOrganizationId(Integer id, UUID organizationId);

    void deleteByIdAndOrganizationId(Integer id, UUID organizationId);
}
