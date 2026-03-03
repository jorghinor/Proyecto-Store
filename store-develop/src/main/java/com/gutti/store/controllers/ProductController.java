package com.gutti.store.controllers;

import com.gutti.store.api.CreateProductPayload;
import com.gutti.store.api.ProductCategoryResponse;
import com.gutti.store.api.ProductResponse;
import com.gutti.store.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.gutti.store.api.AddCategoryRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Jorge Quispe
 */
@RestController
@RequestMapping(ProductController.PATH)
public class ProductController {

    public static final String PATH = Constants.BASE_PATH + "/products";

    @Autowired
    private ProductService productService;

    @PostMapping
    public Long createProduct(@RequestBody CreateProductPayload payload) {
        return productService.createProduct(payload);
    }

    @GetMapping("/{productId}")
    public ProductResponse getProduct(@PathVariable Long productId) {
        return productService.findById(productId);
    }

    @DeleteMapping("/{productId}")
    public void deleteProduct(@PathVariable Long productId) {
        productService.deleteById(productId);
    }

    @PostMapping("/{productId}/categories")
    public void addCategory(@PathVariable Long productId, @RequestBody AddCategoryRequest payload) {
        productService.addCategory(productId, payload.getCategoryId());
    }

    @GetMapping("/{productId}/categories")
    public List<ProductCategoryResponse> getProductCategories(@PathVariable Long productId) {
        return productService.getProductCategories(productId);
    }
}
