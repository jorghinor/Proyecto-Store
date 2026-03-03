package com.gutti.store.services.impl;

import com.gutti.store.configs.AppRequestContext;
import com.gutti.store.domain.*;
import com.gutti.store.dtos.SaveStockItemPropertyDto;
import com.gutti.store.dtos.StockItemPropertyDto;
import com.gutti.store.services.StockItemPropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockItemPropertyServiceImpl implements StockItemPropertyService {

    private final StockItemPropertyRepository repository;
    private final StockItemRepository stockItemRepository;
    private final ItemPropertyRepository itemPropertyRepository;
    private final ItemPropertyValueRepository itemPropertyValueRepository;

    @Override
    @Transactional(readOnly = true)
    public List<StockItemPropertyDto> findAll() {
        UUID organizationId = AppRequestContext.get().getOrganizationId();
        return repository.findAllByOrganizationId(organizationId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StockItemPropertyDto> findById(Integer id) {
        UUID organizationId = AppRequestContext.get().getOrganizationId();
        return repository.findByIdAndOrganizationId(id, organizationId)
                .map(this::toDto);
    }

    @Override
    @Transactional
    public StockItemPropertyDto save(SaveStockItemPropertyDto saveDto) {
        UUID organizationId = AppRequestContext.get().getOrganizationId();
        StockItemProperty entity = toEntity(saveDto);
        entity.setOrganizationId(organizationId);
        return toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public Optional<StockItemPropertyDto> update(Integer id, SaveStockItemPropertyDto saveDto) {
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

    private StockItemPropertyDto toDto(StockItemProperty entity) {
        return StockItemPropertyDto.builder()
                .id(entity.getId())
                .stockItemId(entity.getStockItem().getId())
                .itemPropertyId(entity.getItemProperty().getId())
                .itemPropertyValueId(entity.getItemPropertyValue().getId())
                .build();
    }

    private StockItemProperty toEntity(SaveStockItemPropertyDto dto) {
        StockItemProperty entity = new StockItemProperty();
        updateEntityFromDto(entity, dto);
        return entity;
    }

    private void updateEntityFromDto(StockItemProperty entity, SaveStockItemPropertyDto dto) {
        StockItem stockItem = stockItemRepository.findById(dto.getStockItemId())
                .orElseThrow(() -> new RuntimeException("StockItem not found")); // Consider creating a specific exception
        ItemProperty itemProperty = itemPropertyRepository.findById(dto.getItemPropertyId())
                .orElseThrow(() -> new RuntimeException("ItemProperty not found"));
        ItemPropertyValue itemPropertyValue = itemPropertyValueRepository.findById(dto.getItemPropertyValueId())
                .orElseThrow(() -> new RuntimeException("ItemPropertyValue not found"));

        entity.setStockItem(stockItem);
        entity.setItemProperty(itemProperty);
        entity.setItemPropertyValue(itemPropertyValue);
    }
}
