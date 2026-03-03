package com.gutti.store.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    boolean existsByRolesContains(Role role);

    // --- MÉTODOS PARA PAGINACIÓN Y FILTRADO ---
    @Query("select u from User u where " +
            "lower(u.firstName) like lower(concat('%', :filterText, '%')) or " +
            "lower(u.lastName) like lower(concat('%', :filterText, '%')) or " +
            "lower(u.email) like lower(concat('%', :filterText, '%'))")
    Page<User> searchByFilter(@Param("filterText") String filterText, Pageable pageable);

    @Query("select count(u) from User u where " +
            "lower(u.firstName) like lower(concat('%', :filterText, '%')) or " +
            "lower(u.lastName) like lower(concat('%', :filterText, '%')) or " +
            "lower(u.email) like lower(concat('%', :filterText, '%'))")
    long countByFilter(@Param("filterText") String filterText);
}