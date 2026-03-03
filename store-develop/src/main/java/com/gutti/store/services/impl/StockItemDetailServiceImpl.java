package com.gutti.store.services.impl;

import com.gutti.store.configs.AppRequestContext;
import com.gutti.store.domain.StockItem;
import com.gutti.store.domain.StockItemDetail;
import com.gutti.store.domain.StockItemDetailRepository;
import com.gutti.store.domain.StockItemRepository;
import com.gutti.store.dtos.SaveStockItemDetailDto;
import com.gutti.store.dtos.StockItemDetailDto;
import com.gutti.store.services.StockItemDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockItemDetailServiceImpl implements StockItemDetailService {

    private final StockItemDetailRepository repository;
    private final StockItemRepository stockItemRepository;

    @Override
    @Transactional(readOnly = true)
    public List<StockItemDetailDto> findAll() {
        UUID organizationId = AppRequestContext.get().getOrganizationId();
        return repository.findAllByOrganizationId(organizationId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StockItemDetailDto> findById(Integer id) {
        UUID organizationId = AppRequestContext.get().getOrganizationId();
        return repository.findByIdAndOrganizationId(id, organizationId)
                .map(this::toDto);
    }

    @Override
    @Transactional
    public StockItemDetailDto save(SaveStockItemDetailDto saveDto) {
        UUID organizationId = AppRequestContext.get().getOrganizationId();
        StockItemDetail entity = toEntity(saveDto);
        entity.setOrganizationId(organizationId);
        return toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public Optional<StockItemDetailDto> update(Integer id, SaveStockItemDetailDto saveDto) {
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

    private StockItemDetailDto toDto(StockItemDetail entity) {
        return StockItemDetailDto.builder()
                .id(entity.getId())
                .stockItemId(entity.getStockItem().getId())
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .totalPrice(entity.getTotalPrice())
                .build();
    }

    private StockItemDetail toEntity(SaveStockItemDetailDto dto) {
        StockItemDetail entity = new StockItemDetail();
        updateEntityFromDto(entity, dto);
        return entity;
    }

    private void updateEntityFromDto(StockItemDetail entity, SaveStockItemDetailDto dto) {
        StockItem stockItem = stockItemRepository.findById(dto.getStockItemId())
                .orElseThrow(() -> new RuntimeException("StockItem not found")); // Consider creating a specific exception

        entity.setStockItem(stockItem);
        entity.setQuantity(dto.getQuantity());
        entity.setUnitPrice(dto.getUnitPrice());
        entity.setTotalPrice(dto.getTotalPrice());
    }
}
