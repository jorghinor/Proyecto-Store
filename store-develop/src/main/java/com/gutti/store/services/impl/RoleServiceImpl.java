package com.gutti.store.services.impl;

import com.gutti.store.domain.Role;
import com.gutti.store.domain.RoleRepository;
import com.gutti.store.domain.UserRepository;
import com.gutti.store.dtos.RoleDto;
import com.gutti.store.dtos.SaveRoleDto;
import com.gutti.store.exception.DuplicateResourceException;
import com.gutti.store.exception.ResourceInUseException;
import com.gutti.store.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<RoleDto> fetchPage(String filterText, Pageable pageable) {
        Page<Role> rolePage;
        if (filterText == null || filterText.isEmpty()) {
            rolePage = roleRepository.findAll(pageable);
        } else {
            rolePage = roleRepository.findByNameContainingIgnoreCase(filterText, pageable);
        }
        return rolePage.map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public long count(String filterText) {
        if (filterText == null || filterText.isEmpty()) {
            return roleRepository.count();
        } else {
            return roleRepository.countByNameContainingIgnoreCase(filterText);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleDto> findAll() {
        return roleRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RoleDto> findById(String id) {
        // CORREGIDO: Ya no se convierte a Integer
        return roleRepository.findById(id).map(this::toDto);
    }

    @Override
    @Transactional
    public RoleDto save(SaveRoleDto roleDto) {
        roleRepository.findByName(roleDto.getName()).ifPresent(existingRole -> {
            throw new DuplicateResourceException("El rol '" + roleDto.getName() + "' ya existe.");
        });
        // CORREGIDO: El ID debe ser asignado manualmente. Asumimos que el nombre es el ID.
        Role newRole = new Role();
        newRole.setId(roleDto.getName()); // O la lógica que uses para generar el ID de String
        newRole.setName(roleDto.getName());
        return toDto(roleRepository.save(newRole));
    }

    @Override
    @Transactional
    public Optional<RoleDto> update(String id, SaveRoleDto roleDto) {
        // CORREGIDO: Ya no se convierte a Integer
        return roleRepository.findById(id)
                .map(role -> {
                    role.setName(roleDto.getName());
                    return toDto(roleRepository.save(role));
                });
    }

    @Override
    @Transactional
    public boolean delete(String id) {
        // CORREGIDO: Ya no se convierte a Integer
        return roleRepository.findById(id)
                .map(role -> {
                    if (userRepository.existsByRolesContains(role)) {
                        throw new ResourceInUseException("No se puede eliminar el rol '" + role.getName() + "' porque está asignado a uno o más usuarios.");
                    }
                    roleRepository.delete(role);
                    return true;
                }).orElse(false);
    }

    private RoleDto toDto(Role role) {
        // CORREGIDO: El ID ya es un String
        return new RoleDto(role.getId(), role.getName());
    }
}
