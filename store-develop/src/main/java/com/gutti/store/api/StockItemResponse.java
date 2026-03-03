package com.gutti.store.api;

import com.gutti.store.api.ProductCategoryResponse;
import com.gutti.store.api.ProductResponse;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class StockItemResponse {
    private Integer id;
    private ProductResponse product;
    private ProductCategoryResponse productCategory;
    private List<Map<String, String>> properties;
    private Integer quantity;
    private BigDecimal unitPrice;
}
