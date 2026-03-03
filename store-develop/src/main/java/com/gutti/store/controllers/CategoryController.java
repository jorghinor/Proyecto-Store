package com.gutti.store.controllers;

import com.gutti.store.api.CategoryResponse;
import com.gutti.store.api.CreateCategoryPayload;
import com.gutti.store.api.UpdateCategoryPayload;
import com.gutti.store.configs.AppRequestContext;
import com.gutti.store.domain.Category;
import com.gutti.store.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        UUID organizationId = AppRequestContext.get().getOrganizationId();
        List<Category> categories = categoryService.findAll(organizationId);
        List<CategoryResponse> response = categories.stream()
                .map(categoryService::toCategoryResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.readCategory(id));
    }

    @PostMapping
    public ResponseEntity<Void> createCategory(@RequestBody CreateCategoryPayload payload) {
        Long categoryId = categoryService.createCategory(payload);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(categoryId).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCategory(@PathVariable Long id, @RequestBody UpdateCategoryPayload payload) {
        categoryService.updateCategory(id, payload);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
