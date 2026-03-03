package com.gutti.store.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockItemPropertyDto {
    private Integer id;
    private Integer stockItemId;
    private Integer itemPropertyId;
    private Integer itemPropertyValueId;
}
