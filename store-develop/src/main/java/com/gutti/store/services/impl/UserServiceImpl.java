package com.gutti.store.services.impl;

import com.gutti.store.domain.Role;
import com.gutti.store.domain.RoleRepository;
import com.gutti.store.domain.User;
import com.gutti.store.domain.UserRepository;
import com.gutti.store.dtos.SaveUserDto;
import com.gutti.store.dtos.UserDto;
import com.gutti.store.exception.DuplicateResourceException;
import com.gutti.store.exception.ResourceNotFoundException;
import com.gutti.store.services.UserService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // --- Implementación para la nueva UI de Vaadin ---

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> fetchPage(String filterText, Pageable pageable) {
        Page<User> userPage;
        if (filterText == null || filterText.isEmpty()) {
            userPage = userRepository.findAll(pageable);
        } else {
            userPage = userRepository.searchByFilter(filterText, pageable);
        }
        // Forzamos la carga de los roles para evitar LazyInitializationException
        userPage.getContent().forEach(user -> Hibernate.initialize(user.getRoles()));
        return userPage.map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public long count(String filterText) {
        if (filterText == null || filterText.isEmpty()) {
            return userRepository.count();
        } else {
            return userRepository.countByFilter(filterText);
        }
    }

    // --- Implementación de los métodos existentes ---

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        List<User> users = userRepository.findAll();
        // Forzamos la carga de los roles para evitar LazyInitializationException
        users.forEach(user -> Hibernate.initialize(user.getRoles()));
        return users.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> findById(String id) {
        Optional<User> userOpt = userRepository.findById(id);
        // Forzamos la carga de los roles para evitar LazyInitializationException
        userOpt.ifPresent(user -> Hibernate.initialize(user.getRoles()));
        return userOpt.map(this::toDto);
    }

    @Override
    @Transactional
    public UserDto save(SaveUserDto userDto) {
        userRepository.findByEmail(userDto.getEmail()).ifPresent(u -> {
            throw new DuplicateResourceException("El email '" + userDto.getEmail() + "' ya está en uso.");
        });

        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Set<Role> roles = userDto.getRoles().stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + roleId)))
                .collect(Collectors.toSet());
        user.setRoles(roles);

        return toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public Optional<UserDto> update(String id, SaveUserDto userDto) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setFirstName(userDto.getFirstName());
                    user.setLastName(userDto.getLastName());
                    user.setEmail(userDto.getEmail());
                    if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
                        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
                    }

                    Set<Role> roles = userDto.getRoles().stream()
                            .map(roleId -> roleRepository.findById(roleId)
                                    .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + roleId)))
                            .collect(Collectors.toSet());
                    user.setRoles(roles);

                    return toDto(userRepository.save(user));
                });
    }

    @Override
    @Transactional
    public boolean delete(String id) {
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    return true;
                }).orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    private UserDto toDto(User user) {
        List<String> roleIds = user.getRoles().stream()
                .map(Role::getId)
                .collect(Collectors.toList());
        return new UserDto(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), roleIds);
    }
}
