package com.gutti.store.dtos;

import lombok.Data;

@Data
public class SaveStockItemPropertyDto {
    private Integer stockItemId;
    private Integer itemPropertyId;
    private Integer itemPropertyValueId;
}
