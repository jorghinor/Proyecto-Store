package com.gutti.store.services;

import com.gutti.store.configs.AppRequestContext;
import com.gutti.store.domain.ItemProperty;
import com.gutti.store.domain.ItemPropertyRepository;
import com.gutti.store.domain.ItemPropertyValueRepository;
import com.gutti.store.exception.ResourceInUseException;
import com.gutti.store.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio para gestionar la lógica de negocio de las Propiedades de Items.
 */
@Service
@RequiredArgsConstructor
public class ItemPropertyService {

    private final ItemPropertyRepository itemPropertyRepository;
    private final ItemPropertyValueRepository itemPropertyValueRepository;

    // --- Métodos para la nueva UI de Vaadin (con paginación y filtros) ---

    @Transactional(readOnly = true)
    public Page<ItemProperty> fetchPage(UUID organizationId, String filterText, Pageable pageable) {
        if (filterText == null || filterText.isEmpty()) {
            return itemPropertyRepository.findByOrganizationId(organizationId, pageable);
        } else {
            return itemPropertyRepository.findByOrganizationIdAndLabelContainingIgnoreCase(organizationId, filterText, pageable);
        }
    }

    @Transactional(readOnly = true)
    public long count(UUID organizationId, String filterText) {
        if (filterText == null || filterText.isEmpty()) {
            return itemPropertyRepository.countByOrganizationId(organizationId);
        } else {
            return itemPropertyRepository.countByOrganizationIdAndLabelContainingIgnoreCase(organizationId, filterText);
        }
    }

    @Transactional
    public ItemProperty create(String label, UUID organizationId) {
        return save(new ItemProperty(label, organizationId));
    }

    @Transactional
    public ItemProperty update(Integer id, String label, UUID organizationId) {
        ItemProperty itemProperty = itemPropertyRepository.findById(id)
                .filter(p -> p.getOrganizationId().equals(organizationId))
                .orElseThrow(() -> new ResourceNotFoundException("Propiedad no encontrada con id: " + id));
        itemProperty.setLabel(label);
        return itemPropertyRepository.save(itemProperty);
    }

    @Transactional
    public void delete(Integer id, UUID organizationId) {
        ItemProperty itemProperty = itemPropertyRepository.findById(id)
                .filter(p -> p.getOrganizationId().equals(organizationId))
                .orElseThrow(() -> new ResourceNotFoundException("Propiedad no encontrada con id: " + id));

        if (itemPropertyValueRepository.existsByItemProperty(itemProperty)) {
            throw new ResourceInUseException("No se puede eliminar la propiedad '" + itemProperty.getLabel() + "' porque tiene valores asociados.");
        }

        itemPropertyRepository.delete(itemProperty);
    }

    // --- Métodos para mantener compatibilidad con el API Controller original ---

    @Transactional(readOnly = true)
    public List<ItemProperty> findAll() {
        UUID organizationId = AppRequestContext.get().getOrganizationId();
        return itemPropertyRepository.findByOrganizationId(organizationId);
    }

    @Transactional(readOnly = true)
    public Optional<ItemProperty> findById(Integer id) {
        UUID organizationId = AppRequestContext.get().getOrganizationId();
        return itemPropertyRepository.findById(id)
                .filter(p -> p.getOrganizationId().equals(organizationId));
    }

    @Transactional
    public ItemProperty save(ItemProperty property) {
        // Si es una nueva entidad, nos aseguramos de que tenga el organizationId del contexto
        if (property.getOrganizationId() == null) {
            UUID organizationId = AppRequestContext.get().getOrganizationId();
            property.setOrganizationId(organizationId);
        }
        return itemPropertyRepository.save(property);
    }

    @Transactional
    public void deleteById(Integer id) {
        UUID organizationId = AppRequestContext.get().getOrganizationId();
        this.delete(id, organizationId);
    }
}
