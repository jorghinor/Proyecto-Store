package com.gutti.store.services;

import com.gutti.store.api.CategoryResponse;
import com.gutti.store.api.CreateCategoryPayload;
import com.gutti.store.api.UpdateCategoryPayload;
import com.gutti.store.configs.AppRequestContext;
import com.gutti.store.domain.*;
import com.gutti.store.exception.CategoryUsedInProductsException;
import com.gutti.store.exception.DuplicateCategoryException;
import com.gutti.store.exception.ResourceNotFoundException;
import com.gutti.store.exception.UpdateDuplicateCategoryException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final CategoryTypeService categoryTypeService;
    private final OrganizationService organizationService; // Servicio añadido

    @Transactional(readOnly = true)
    public CategoryResponse readCategory(Long id) {
        return toCategoryResponse(findCategory(id));
    }

    @Transactional(readOnly = true)
    public List<Category> findAll(UUID organizationId) {
        return categoryRepository.findByOrganizationIdAndDeletedIsFalseOrderByLabelAsc(organizationId);
    }

    @Transactional(readOnly = true)
    public Page<Category> fetchPage(UUID organizationId, String filterText, Pageable pageable) {
        if (filterText == null || filterText.isEmpty()) {
            return categoryRepository.findByOrganizationIdAndDeletedIsFalse(organizationId, pageable);
        } else {
            return categoryRepository.findByOrganizationIdAndLabelContainingIgnoreCaseAndDeletedIsFalse(organizationId, filterText, pageable);
        }
    }

    @Transactional(readOnly = true)
    public long count(UUID organizationId, String filterText) {
        if (filterText == null || filterText.isEmpty()) {
            return categoryRepository.countByOrganizationIdAndDeletedIsFalse(organizationId);
        } else {
            return categoryRepository.countByOrganizationIdAndLabelContainingIgnoreCaseAndDeletedIsFalse(organizationId, filterText);
        }
    }

    public CategoryResponse toCategoryResponse(Category source) {
        return CategoryResponse.builder()
                .id(source.getId())
                .label(source.getLabel())
                .categoryType(categoryTypeService.toCategoryTypeResponse(source.getCategoryType()))
                .build();
    }

    @Transactional
    public Long createCategory(CreateCategoryPayload payload) {
        CategoryType categoryType = CategoryType.valueOf(payload.getCategoryTypeId());

        UUID organizationId = AppRequestContext.get().getOrganizationId();
        Organization organization = organizationService.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        categoryRepository.findByLabelAndCategoryType(payload.getLabel(), categoryType)
                .ifPresent(c -> {
                    throw new DuplicateCategoryException(payload.getLabel(), payload.getCategoryTypeId());
                });

        Category instance = new Category();
        instance.setCategoryType(categoryType);
        instance.setLabel(payload.getLabel());
        instance.setOrganization(organization); // Asignamos el objeto
        instance.setDeleted(false);

        instance = categoryRepository.save(instance);
        return instance.getId();
    }

    @Transactional
    public void updateCategory(Long categoryId, UpdateCategoryPayload payload) {
        Category category = findCategory(categoryId);

        categoryRepository.findByLabelIgnoreCase(payload.getLabel()).ifPresent(foundCategory -> {
            if (!foundCategory.getId().equals(category.getId())) {
                throw new UpdateDuplicateCategoryException(payload.getLabel());
            }
        });

        category.setLabel(payload.getLabel());
        categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = findCategory(categoryId);
        UUID organizationId = AppRequestContext.get().getOrganizationId();
        if (productCategoryRepository.existsByCategoryId(categoryId, organizationId)) {
            throw new CategoryUsedInProductsException(category.getLabel());
        }
        category.setDeleted(true);
        categoryRepository.save(category);
    }

    private Category findCategory(Long id) {
        UUID organizationId = AppRequestContext.get().getOrganizationId();
        return categoryRepository.findByIdAndOrganizationIdAndDeletedIsFalse(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<ProductCategory> findAllProductCategories(UUID organizationId) {
        List<ProductCategory> productCategories = productCategoryRepository.findAllByOrganizationId(organizationId);
        productCategories.forEach(pc -> Hibernate.initialize(pc.getCategory()));
        return productCategories;
    }
}