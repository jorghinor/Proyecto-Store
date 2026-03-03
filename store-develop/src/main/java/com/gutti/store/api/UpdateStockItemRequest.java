package com.gutti.store.api;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UpdateStockItemRequest {
    private Integer quantity;
    private BigDecimal unitPrice;
    private List<Integer> propertyValueIds;
}