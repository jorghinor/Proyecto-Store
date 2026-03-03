package com.gutti.store.controllers;

import com.gutti.store.dtos.ProductPropertyDto;
import com.gutti.store.dtos.SaveProductPropertyDto;
import com.gutti.store.services.ProductPropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ProductPropertyController.PATH)
@RequiredArgsConstructor
public class ProductPropertyController {

    public static final String PATH = Constants.BASE_PATH + "/product-properties";

    private final ProductPropertyService service;

    @GetMapping
    public ResponseEntity<List<ProductPropertyDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductPropertyDto> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProductPropertyDto> save(@RequestBody SaveProductPropertyDto saveDto) {
        ProductPropertyDto savedDto = service.save(saveDto);
        return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductPropertyDto> update(@PathVariable Integer id, @RequestBody SaveProductPropertyDto saveDto) {
        return service.update(id, saveDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        boolean deleted = service.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
