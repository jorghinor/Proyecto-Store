package com.gutti.store.services;

import com.gutti.store.dtos.ProductPropertyDto;
import com.gutti.store.dtos.SaveProductPropertyDto;

import java.util.List;
import java.util.Optional;

public interface ProductPropertyService {

    List<ProductPropertyDto> findAll();

    Optional<ProductPropertyDto> findById(Integer id);

    ProductPropertyDto save(SaveProductPropertyDto productPropertyDto);

    Optional<ProductPropertyDto> update(Integer id, SaveProductPropertyDto productPropertyDto);

    boolean delete(Integer id);
}
