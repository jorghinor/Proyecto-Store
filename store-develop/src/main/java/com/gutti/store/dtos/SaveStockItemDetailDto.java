package com.gutti.store.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaveStockItemDetailDto {
    private Integer stockItemId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
