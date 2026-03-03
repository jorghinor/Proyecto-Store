package com.gutti.store.services;

import com.gutti.store.api.CreateStockItemRequest;
import com.gutti.store.api.UpdateStockItemRequest;
import com.gutti.store.domain.*;
import com.gutti.store.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
//import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockItemRepository stockItemRepository;
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ItemPropertyValueRepository itemPropertyValueRepository;

    @Transactional
    public StockItem createStockItem(CreateStockItemRequest request, UUID organizationId) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        ProductCategory productCategory = productCategoryRepository.findById(request.getProductCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found with id: " + request.getProductCategoryId()));

        StockItem stockItem = new StockItem();
        stockItem.setProduct(product);
        stockItem.setProductCategory(productCategory);
        stockItem.setOrganizationId(organizationId);

        StockItemDetail detail = new StockItemDetail();
        detail.setStockItem(stockItem);
        detail.setQuantity(request.getQuantity());
        detail.setUnitPrice(request.getUnitPrice());
        detail.setTotalPrice(request.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
        detail.setOrganizationId(organizationId);
        stockItem.setDetails(List.of(detail));

        if (request.getPropertyValueIds() != null && !request.getPropertyValueIds().isEmpty()) {
            List<StockItemProperty> properties = request.getPropertyValueIds().stream()
                    .map(propValId -> {
                        ItemPropertyValue propVal = itemPropertyValueRepository.findById(propValId)
                                .orElseThrow(() -> new ResourceNotFoundException("Property value not found with id: " + propValId));

                        StockItemProperty stockProp = new StockItemProperty();
                        stockProp.setStockItem(stockItem);
                        stockProp.setItemProperty(propVal.getItemProperty());
                        stockProp.setItemPropertyValue(propVal);
                        stockProp.setOrganizationId(organizationId);
                        return stockProp;
                    })
                    .collect(Collectors.toList());
            stockItem.setProperties(properties);
        }

        StockItem savedStockItem = stockItemRepository.save(stockItem);

        // --- INICIALIZACIÓN EXPLÍCITA Y ROBUSTA ---
        if (savedStockItem.getProperties() != null) {
            Hibernate.initialize(savedStockItem.getProperties());
            for (StockItemProperty prop : savedStockItem.getProperties()) {
                Hibernate.initialize(prop.getItemProperty());
                Hibernate.initialize(prop.getItemPropertyValue());
            }
        }
        if (savedStockItem.getDetails() != null) {
            Hibernate.initialize(savedStockItem.getDetails());
        }
        // --------------------------------------------

        return savedStockItem;
    }

    @Transactional(readOnly = true)
    public List<StockItem> findAll(UUID organizationId) {
        return findAll(organizationId, null);
    }

    @Transactional(readOnly = true)
    public List<StockItem> findAll(UUID organizationId, String filterText) {
        Page<StockItem> page = fetchPage(organizationId, filterText, Pageable.unpaged());
        List<StockItem> stockItems = new ArrayList<>(page.getContent());
        initializeStockItems(stockItems);
        return stockItems;
    }

    @Transactional(readOnly = true)
    public Page<StockItem> fetchPage(UUID organizationId, String filterText, Pageable pageable) {
        Page<StockItem> page;
        if (filterText == null || filterText.isEmpty()) {
            page = stockItemRepository.findByOrganizationId(organizationId, pageable);
        } else {
            page = stockItemRepository.search(organizationId, filterText, pageable);
        }
        // Inicializa las entidades de la página actual
        initializeStockItems(page.getContent());
        return page;
    }

    @Transactional(readOnly = true)
    public long count(UUID organizationId, String filterText) {
        if (filterText == null || filterText.isEmpty()) {
            return stockItemRepository.countByOrganizationId(organizationId);
        } else {
            return stockItemRepository.countSearch(organizationId, filterText);
        }
    }

    private void initializeStockItems(List<StockItem> stockItems) {
        for (StockItem stockItem : stockItems) {
            Hibernate.initialize(stockItem.getProduct());
            if (stockItem.getProduct() != null) {
                Hibernate.initialize(stockItem.getProduct().getCategories());
                stockItem.getProduct().getCategories().forEach(pc -> Hibernate.initialize(pc.getCategory()));
            }
            Hibernate.initialize(stockItem.getDetails());
            Hibernate.initialize(stockItem.getProductCategory());
        }
    }

    @Transactional(readOnly = true)
    public StockItem findById(Integer id, UUID organizationId) {
        StockItem stockItem = stockItemRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("StockItem not found with id: " + id));

        // --- INICIALIZACIÓN PROFUNDA Y EXPLÍCITA ---
        Hibernate.initialize(stockItem.getProduct());
        if (stockItem.getProduct() != null) {
            Hibernate.initialize(stockItem.getProduct().getCategories());
            stockItem.getProduct().getCategories().forEach(pc -> Hibernate.initialize(pc.getCategory()));
        }
        Hibernate.initialize(stockItem.getProductCategory());

        if (stockItem.getProperties() != null) {
            Hibernate.initialize(stockItem.getProperties());
            for (StockItemProperty prop : stockItem.getProperties()) {
                Hibernate.initialize(prop.getItemProperty());
                Hibernate.initialize(prop.getItemPropertyValue());
            }
        }
        if (stockItem.getDetails() != null) {
            Hibernate.initialize(stockItem.getDetails());
        }

        return stockItem;
    }

    @Transactional
    public StockItem updateStockItem(Integer id, UpdateStockItemRequest request, UUID organizationId) {
        StockItem stockItem = findById(id, organizationId);

        if (stockItem.getDetails() != null && !stockItem.getDetails().isEmpty()) {
            StockItemDetail detail = stockItem.getDetails().getFirst();
            if (request.getQuantity() != null) {
                detail.setQuantity(request.getQuantity());
            }
            if (request.getUnitPrice() != null) {
                detail.setUnitPrice(request.getUnitPrice());
            }
            detail.setTotalPrice(detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity())));
        }

        if (request.getPropertyValueIds() != null) {
            stockItem.getProperties().clear();

            if (!request.getPropertyValueIds().isEmpty()) {
                List<StockItemProperty> newProperties = request.getPropertyValueIds().stream()
                        .map(propValId -> {
                            ItemPropertyValue propVal = itemPropertyValueRepository.findById(propValId)
                                    .orElseThrow(() -> new ResourceNotFoundException("Property value not found with id: " + propValId));

                            StockItemProperty stockProp = new StockItemProperty();
                            stockProp.setStockItem(stockItem);
                            stockProp.setItemProperty(propVal.getItemProperty());
                            stockProp.setItemPropertyValue(propVal);
                            stockProp.setOrganizationId(organizationId);
                            return stockProp;
                        })
                        .toList();
                stockItem.getProperties().addAll(newProperties);
            }
        }

        return stockItemRepository.save(stockItem);
    }

    @Transactional
    public void deleteById(Integer id, UUID organizationId) {
        StockItem stockItem = findById(id, organizationId);
        stockItemRepository.delete(stockItem);
    }
}

