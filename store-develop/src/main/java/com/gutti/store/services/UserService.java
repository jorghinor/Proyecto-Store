package com.gutti.store.services;

import com.gutti.store.domain.User;
import com.gutti.store.dtos.SaveUserDto;
import com.gutti.store.dtos.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {

    // --- Métodos para la nueva UI de Vaadin ---
    Page<UserDto> fetchPage(String filterText, Pageable pageable);

    long count(String filterText);

    // --- Métodos existentes para el API Controller ---
    List<UserDto> findAll();

    Optional<UserDto> findById(String id);

    UserDto save(SaveUserDto userDto);

    Optional<UserDto> update(String id, SaveUserDto userDto);

    boolean delete(String id);

    Optional<User> findByEmail(String email);
}
