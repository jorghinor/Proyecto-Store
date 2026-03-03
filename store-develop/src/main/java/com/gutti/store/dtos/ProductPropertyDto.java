package com.gutti.store.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductPropertyDto {
    private Integer id;
    private Long productId;
    private Integer itemPropertyId;
}
