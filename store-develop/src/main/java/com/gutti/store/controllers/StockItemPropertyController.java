package com.gutti.store.controllers;

import com.gutti.store.dtos.SaveStockItemPropertyDto;
import com.gutti.store.dtos.StockItemPropertyDto;
import com.gutti.store.services.StockItemPropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(StockItemPropertyController.PATH)
@RequiredArgsConstructor
public class StockItemPropertyController {

    public static final String PATH = Constants.BASE_PATH + "/stock-item-properties";

    private final StockItemPropertyService service;

    @GetMapping
    public ResponseEntity<List<StockItemPropertyDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockItemPropertyDto> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StockItemPropertyDto> save(@RequestBody SaveStockItemPropertyDto saveDto) {
        StockItemPropertyDto savedDto = service.save(saveDto);
        return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockItemPropertyDto> update(@PathVariable Integer id, @RequestBody SaveStockItemPropertyDto saveDto) {
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
