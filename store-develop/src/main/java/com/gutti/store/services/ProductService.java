package com.gutti.store.services;

import com.gutti.store.api.CreateProductPayload;
import com.gutti.store.api.ProductCategoryResponse;
import com.gutti.store.api.ProductResponse;
import com.gutti.store.configs.AppRequestContext;
import com.gutti.store.domain.*;
import com.gutti.store.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Jorge Quispe Mpye
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final CategoryService categoryService;
    private final OrganizationService organizationService;

    // --- MÉTODOS PARA LA VISTA DE VAADIN ---

    @Transactional(readOnly = true)
    public Page<Product> fetchPage(String filterText, Pageable pageable) {
        UUID organizationId = AppRequestContext.get().getOrganizationId();
        if (filterText == null || filterText.isEmpty()) {
            return productRepository.findByOrganizationIdAndDeletedIsFalse(organizationId, pageable);
        } else {
            return productRepository.findByOrganizationIdAndNameContainingIgnoreCaseAndDeletedIsFalse(
                    organizationId, filterText, pageable);
        }
    }

    @Transactional(readOnly = true)
    public long count(String filterText) {
        UUID organizationId = AppRequestContext.get().getOrganizationId();
        if (filterText == null || filterText.isEmpty()) {
            return productRepository.countByOrganizationIdAndDeletedIsFalse(organizationId);
        } else {
            return productRepository.countByOrganizationIdAndNameContainingIgnoreCaseAndDeletedIsFalse(organizationId, filterText);
        }
    }

    @Transactional
    public Optional<Product> update(Long id, Product productUpdate) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    existingProduct.setName(productUpdate.getName());
                    existingProduct.setBrand(productUpdate.getBrand());
                    existingProduct.setImageUrl(productUpdate.getImageUrl());
                    existingProduct.setPrice(productUpdate.getPrice()); // Actualizar precio
                    // Guardamos los nuevos campos de oferta
                    existingProduct.setDiscountPercentage(productUpdate.getDiscountPercentage());
                    existingProduct.setOnPromotion(productUpdate.isOnPromotion());
                    existingProduct.setNewArrival(productUpdate.isNewArrival());
                    return productRepository.save(existingProduct);
                });
    }

    @Transactional
    public Product save(Product product) {
        UUID organizationId = AppRequestContext.get().getOrganizationId();
        Organization organization = organizationService.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
        product.setOrganization(organization);
        product.setDeleted(false);
        return productRepository.save(product);
    }

    // --- MÉTODOS EXISTENTES (ADAPTADOS O MANTENIDOS) ---

    public Long createProduct(CreateProductPayload payload) {
        Optional<Product> existingProduct = productRepository.findByNameIgnoreCase(payload.getName());
        if (existingProduct.isPresent()) {
            throw new DuplicateProductException(payload.getName());
        }

        UUID organizationId = AppRequestContext.get().getOrganizationId();
        Organization organization = organizationService.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        Product instance = new Product();
        instance.setName(payload.getName());
        instance.setBrand(payload.getBrand());
        instance.setImageUrl(payload.getImageUrl());
        instance.setPrice(payload.getPrice()); // Guardar precio
        instance.setOrganization(organization);
        instance.setDeleted(false);

        instance = productRepository.save(instance);
        return instance.getId();
    }

    public ProductResponse findById(Long id) {
        return productRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public void deleteById(Long id) {
        productRepository.findById(id)
                .ifPresentOrElse(product -> {
                    UUID organizationId = AppRequestContext.get().getOrganizationId();
                    if (productCategoryRepository.existsByProductId(id, organizationId)) {
                        throw new ProductHasCategoriesException(product.getName());
                    }
                    product.setDeleted(true);
                    productRepository.save(product);
                }, () -> {
                    throw new ProductNotFoundException(id);
                });
    }

    public void addCategory(Long productId, Long categoryId) {
        Product product = productRepository.findByIdAndDeletedIsFalse(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        Category category = categoryRepository.findByIdAndDeletedIsFalse(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        UUID organizationId = AppRequestContext.get().getOrganizationId();
        Optional<ProductCategory> productCategory = productCategoryRepository.findByProductIdAndCategoryId(productId, categoryId, organizationId);
        if (productCategory.isPresent()) {
            throw new ProductAlreadyHasCategoryException();
        }

        ProductCategory instance = new ProductCategory();
        instance.setProduct(product);
        instance.setCategory(category);
        instance.setOrganizationId(organizationId);
        productCategoryRepository.save(instance);
    }

    public List<ProductCategoryResponse> getProductCategories(Long productId) {
        productRepository.findByIdAndDeletedIsFalse(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        UUID organizationId = AppRequestContext.get().getOrganizationId();
        return productCategoryRepository.findAllByProductId(productId, organizationId)
                .stream()
                .map(productCategory -> {
                    // Obtenemos la categoría directamente de la relación
                    Category category = productCategory.getCategory();
                    return ProductCategoryResponse.builder()
                            .id(productCategory.getId())
                            .category(categoryService.toCategoryResponse(category))
                            .build();
                })
                .sorted(Comparator.comparing(pcr -> pcr.getCategory().getLabel()))
                .collect(Collectors.toList());
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .build();
    }

    @Transactional(readOnly = true)
    public List<Product> findAll(UUID organizationId) {
        List<Product> products = productRepository.findByOrganizationIdAndDeletedIsFalse(organizationId);
        products.forEach(product -> {
            Hibernate.initialize(product.getCategories());
            product.getCategories().forEach(pc -> Hibernate.initialize(pc.getCategory()));
        });
        return products;
    }

    // --- MÉTODOS PARA EL CATÁLOGO ---

    @Transactional(readOnly = true)
    public Page<Product> fetchCatalogPage(String filterText, Pageable pageable) {
        UUID organizationId = AppRequestContext.get().getOrganizationId();
        Page<Product> productPage;

        if (filterText == null || filterText.isEmpty()) {
            productPage = productRepository.findByOrganizationIdAndDeletedIsFalse(organizationId, pageable);
        } else {
            productPage = productRepository.findByOrganizationIdAndFilterText(organizationId, filterText, pageable);
        }

        // Forzamos la carga de la organización para cada producto de la página actual.
        productPage.getContent().forEach(product -> Hibernate.initialize(product.getOrganization()));

        return productPage;
    }

    @Transactional(readOnly = true)
    public long countCatalog(String filterText) {
        UUID organizationId = AppRequestContext.get().getOrganizationId();
        return (filterText == null || filterText.isEmpty())
                ? productRepository.countByOrganizationIdAndDeletedIsFalse(organizationId)
                : productRepository.countByOrganizationIdAndFilterText(organizationId, filterText);
    }
}
