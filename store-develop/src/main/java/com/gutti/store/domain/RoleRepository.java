package com.gutti.store.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
// CORREGIDO: JpaRepository<Role, String>
public interface RoleRepository extends JpaRepository<Role, String> {

    Optional<Role> findByName(String name);

    Page<Role> findByNameContainingIgnoreCase(String name, Pageable pageable);

    long countByNameContainingIgnoreCase(String name);
}
