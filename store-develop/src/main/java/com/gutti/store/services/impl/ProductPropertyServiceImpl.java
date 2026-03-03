package com.gutti.store.services.impl;

import com.gutti.store.configs.AppRequestContext;
import com.gutti.store.domain.*;
import com.gutti.store.dtos.ProductPropertyDto;
import com.gutti.store.dtos.SaveProductPropertyDto;
import com.gutti.store.services.ProductPropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductPropertyServiceImpl implements ProductPropertyService {

    private final ProductPropertyRepository repository;
    private final ProductRepository productRepository;
    private final ItemPropertyRepository itemPropertyRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductPropertyDto> findAll() {
        UUID organizationId = AppRequestContext.get().getOrganizationId();
        return repository.findAllByOrganizationId(organizationId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductPropertyDto> findById(Integer id) {
        UUID organizationId = AppRequestContext.get().getOrganizationId();
        return repository.findByIdAndOrganizationId(id, organizationId)
                .map(this::toDto);
    }

    @Override
    @Transactional
    public ProductPropertyDto save(SaveProductPropertyDto saveDto) {
        UUID organizationId = AppRequestContext.get().getOrganizationId();
        ProductProperty entity = toEntity(saveDto);
        entity.setOrganizationId(organizationId);
        return toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public Optional<ProductPropertyDto> update(Integer id, SaveProductPropertyDto saveDto) {
        UUID organizationId = AppRequestContext.get().getOrganizationId();
        return repository.findByIdAndOrganizationId(id, organizationId)
                .map(existingEntity -> {
                    updateEntityFromDto(existingEntity, saveDto);
                    return toDto(repository.save(existingEntity));
                });
    }

    @Override
    @Transactional
    public boolean delete(Integer id) {
        UUID organizationId = AppRequestContext.get().getOrganizationId();
        return repository.findByIdAndOrganizationId(id, organizationId)
                .map(entity -> {
                    repository.delete(entity);
                    return true;
                }).orElse(false);
    }

    private ProductPropertyDto toDto(ProductProperty entity) {
        return ProductPropertyDto.builder()
                .id(entity.getId())
                .productId(entity.getProduct().getId())
                .itemPropertyId(entity.getItemProperty().getId())
                .build();
    }

    private ProductProperty toEntity(SaveProductPropertyDto dto) {
        ProductProperty entity = new ProductProperty();
        updateEntityFromDto(entity, dto);
        return entity;
    }

    private void updateEntityFromDto(ProductProperty entity, SaveProductPropertyDto dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found")); // Consider creating a specific exception
        ItemProperty itemProperty = itemPropertyRepository.findById(dto.getItemPropertyId())
                .orElseThrow(() -> new RuntimeException("ItemProperty not found"));

        entity.setProduct(product);
        entity.setItemProperty(itemProperty);
    }
}
