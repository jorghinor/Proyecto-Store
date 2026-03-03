package com.gutti.store.services;

import com.gutti.store.dtos.RoleDto;
import com.gutti.store.dtos.SaveRoleDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RoleService {

    Page<RoleDto> fetchPage(String filterText, Pageable pageable);
    long count(String filterText);

    List<RoleDto> findAll();

    // CORREGIDO: El ID es un String
    Optional<RoleDto> findById(String id);

    RoleDto save(SaveRoleDto roleDto);

    // CORREGIDO: El ID es un String
    Optional<RoleDto> update(String id, SaveRoleDto roleDto);

    // CORREGIDO: El ID es un String
    boolean delete(String id);
}
