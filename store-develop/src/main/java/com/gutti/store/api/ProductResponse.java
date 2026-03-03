package com.gutti.store.api;

import lombok.*;

/**
 * @author Jorge Quispe
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {

    private Long id;

    private String name;
}
