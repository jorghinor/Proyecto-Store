package com.gutti.store.controllers;

import com.gutti.store.dtos.SaveStockItemDetailDto;
import com.gutti.store.dtos.StockItemDetailDto;
import com.gutti.store.services.StockItemDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(StockItemDetailController.PATH)
@RequiredArgsConstructor
public class StockItemDetailController {

    public static final String PATH = Constants.BASE_PATH + "/stock-item-details";

    private final StockItemDetailService service;

    @GetMapping
    public ResponseEntity<List<StockItemDetailDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockItemDetailDto> findById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StockItemDetailDto> save(@RequestBody SaveStockItemDetailDto saveDto) {
        StockItemDetailDto savedDto = service.save(saveDto);
        return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockItemDetailDto> update(@PathVariable Integer id, @RequestBody SaveStockItemDetailDto saveDto) {
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
