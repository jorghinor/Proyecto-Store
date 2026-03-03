package com.gutti.store.api;

import lombok.*;

/**
 * @author Ivan Alban
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {

    private Long id;

    private String label;

    private CategoryTypeResponse categoryType;
}
