package com.gutti.store.api;

import lombok.*;

import java.math.BigDecimal;

/**
 * @author Jorge Quispe
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductPayload {

    private String name;
    private String brand;
    private String imageUrl;
    private BigDecimal price;
}
