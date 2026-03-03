package com.gutti.store.controllers;

import com.gutti.store.api.CreateStockItemRequest;
import com.gutti.store.api.UpdateStockItemRequest;
import com.gutti.store.api.StockItemResponse;
import com.gutti.store.api.StockMapper;
import com.gutti.store.configs.AppRequestContext;
import com.gutti.store.domain.StockItem;
import com.gutti.store.services.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping(StockController.PATH)
@RequiredArgsConstructor
public class StockController {
    public static final String PATH = Constants.BASE_PATH + "/stock-items";

    private final StockService stockService;

    @PostMapping
    public ResponseEntity<StockItemResponse> createStockItem(@RequestBody CreateStockItemRequest request) {
        StockItem stockItem = stockService.createStockItem(request, AppRequestContext.get().getOrganizationId());
        return ResponseEntity.status(HttpStatus.CREATED).body(StockMapper.toStockItemResponse(stockItem));
    }

    @GetMapping
    public ResponseEntity<List<StockItemResponse>> findAll() {
        List<StockItem> stockItems = stockService.findAll(AppRequestContext.get().getOrganizationId());
        List<StockItemResponse> responses = stockItems.stream()
                .map(StockMapper::toStockItemResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockItemResponse> findById(@PathVariable Integer id) {
        StockItem stockItem = stockService.findById(id, AppRequestContext.get().getOrganizationId());
        return ResponseEntity.ok(StockMapper.toStockItemResponse(stockItem));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockItemResponse> updateStockItem(
            @PathVariable Integer id,
            @RequestBody UpdateStockItemRequest request) {
        StockItem stockItem = stockService.updateStockItem(id, request, AppRequestContext.get().getOrganizationId());
        return ResponseEntity.ok(StockMapper.toStockItemResponse(stockItem));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStockItem(@PathVariable Integer id) {
        stockService.deleteById(id, AppRequestContext.get().getOrganizationId());
        return ResponseEntity.noContent().build();
    }
}

