package com.gutti.store.services;

import com.gutti.store.configs.AppRequestContext;
import com.gutti.store.domain.ItemProperty;
import com.gutti.store.domain.ItemPropertyRepository;
import com.gutti.store.domain.ItemPropertyValue;
import com.gutti.store.domain.ItemPropertyValueRepository;
import com.gutti.store.domain.StockItemPropertyRepository;
import com.gutti.store.exception.ResourceInUseException;
import com.gutti.store.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemPropertyValueService {

    private final ItemPropertyValueRepository itemPropertyValueRepository;
    private final ItemPropertyRepository itemPropertyRepository;
    private final StockItemPropertyRepository stockItemPropertyRepository;

    @Transactional(readOnly = true)
    public Page<ItemPropertyValue> fetchPage(UUID organizationId, String filterText, Pageable pageable) {
        Page<ItemPropertyValue> page;
        if (filterText == null || filterText.isEmpty()) {
            page = itemPropertyValueRepository.findByOrganizationId(organizationId, pageable);
        } else {
            page = itemPropertyValueRepository.findByOrganizationIdAndValueContainingIgnoreCase(organizationId, filterText, pageable);
        }
        page.getContent().forEach(value -> Hibernate.initialize(value.getItemProperty()));
        return page;
    }

    @Transactional(readOnly = true)
    public long count(UUID organizationId, String filterText) {
        if (filterText == null || filterText.isEmpty()) {
            return itemPropertyValueRepository.countByOrganizationId(organizationId);
        } else {
            return itemPropertyValueRepository.countByOrganizationIdAndValueContainingIgnoreCase(organizationId, filterText);
        }
    }

    @Transactional
    public ItemPropertyValue create(Integer propertyId, String value, UUID organizationId) {
        ItemProperty itemProperty = itemPropertyRepository.findById(propertyId)
                .filter(p -> p.getOrganizationId().equals(organizationId))
                .orElseThrow(() -> new ResourceNotFoundException("Propiedad no encontrada con id: " + propertyId));

        ItemPropertyValue newValue = new ItemPropertyValue();
        newValue.setItemProperty(itemProperty);
        newValue.setValue(value);
        newValue.setOrganizationId(organizationId);
        return itemPropertyValueRepository.save(newValue);
    }

    @Transactional
    public ItemPropertyValue update(Integer valueId, String value, UUID organizationId) {
        ItemPropertyValue itemValue = itemPropertyValueRepository.findById(valueId)
                .filter(v -> v.getOrganizationId().equals(organizationId))
                .orElseThrow(() -> new ResourceNotFoundException("Valor de propiedad no encontrado con id: " + valueId));

        itemValue.setValue(value);
        return itemPropertyValueRepository.save(itemValue);
    }

    @Transactional
    public void delete(Integer valueId, UUID organizationId) {
        ItemPropertyValue itemValue = itemPropertyValueRepository.findById(valueId)
                .filter(v -> v.getOrganizationId().equals(organizationId))
                .orElseThrow(() -> new ResourceNotFoundException("Valor de propiedad no encontrado con id: " + valueId));

        if (stockItemPropertyRepository.existsByItemPropertyValue(itemValue)) {
            throw new ResourceInUseException("No se puede eliminar el valor '" + itemValue.getValue() + "' porque está en uso por un item de stock.");
        }

        itemPropertyValueRepository.delete(itemValue);
    }

    // --- Métodos para mantener compatibilidad con el API Controller original ---

    @Transactional(readOnly = true)
    public List<ItemPropertyValue> findAll() {
        UUID organizationId = AppRequestContext.get().getOrganizationId();
        List<ItemPropertyValue> values = itemPropertyValueRepository.findByOrganizationId(organizationId, Pageable.unpaged()).getContent();
        values.forEach(value -> Hibernate.initialize(value.getItemProperty()));
        return values;
    }

    @Transactional(readOnly = true)
    public Optional<ItemPropertyValue> findById(Integer id) {
        UUID organizationId = AppRequestContext.get().getOrganizationId();
        Optional<ItemPropertyValue> valueOpt = itemPropertyValueRepository.findById(id)
                .filter(v -> v.getOrganizationId().equals(organizationId));
        valueOpt.ifPresent(value -> Hibernate.initialize(value.getItemProperty()));
        return valueOpt;
    }

    @Transactional
    public ItemPropertyValue save(ItemPropertyValue value) {
        if (value.getOrganizationId() == null) {
            UUID organizationId = AppRequestContext.get().getOrganizationId();
            value.setOrganizationId(organizationId);
        }
        if (value.getItemProperty() != null && value.getItemProperty().getId() != null) {
            ItemProperty parentProperty = itemPropertyRepository.findById(value.getItemProperty().getId())
                    .filter(p -> p.getOrganizationId().equals(value.getOrganizationId()))
                    .orElseThrow(() -> new ResourceNotFoundException("La propiedad padre no existe o no pertenece a la organización."));
            value.setItemProperty(parentProperty);
        }
        return itemPropertyValueRepository.save(value);
    }

    @Transactional
    public void deleteById(Integer id) {
        UUID organizationId = AppRequestContext.get().getOrganizationId();
        this.delete(id, organizationId);
    }
}