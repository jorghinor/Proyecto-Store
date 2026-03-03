package com.gutti.store.api;

import com.gutti.store.api.ProductCategoryResponse;
import com.gutti.store.api.ProductResponse;
import com.gutti.store.domain.StockItem;
import com.gutti.store.domain.StockItemDetail;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class StockMapper {

    public static StockItemResponse toStockItemResponse(StockItem stockItem) {
        return StockItemResponse.builder()
                .id(stockItem.getId())
                .product(ProductResponse.builder()
                        .id(stockItem.getProduct().getId())
                        .name(stockItem.getProduct().getName())
                        .build())
                .productCategory(ProductCategoryResponse.builder()
                        .id(stockItem.getProductCategory().getId())
                        .build())
                .properties(stockItem.getProperties().stream()
                        .map(p -> {
                            Map<String, String> props = new HashMap<>();
                            props.put(p.getItemProperty().getLabel(), p.getItemPropertyValue().getValue());
                            return props;
                        })
                        .collect(Collectors.toList()))
                .quantity(stockItem.getDetails() != null ? stockItem.getDetails().stream().mapToInt(StockItemDetail::getQuantity).sum() : 0)
                .unitPrice(stockItem.getDetails() != null && !stockItem.getDetails().isEmpty() ? stockItem.getDetails().getFirst().getUnitPrice() : null)
                .build();
    }
}
