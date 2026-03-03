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
public class CategoryTypeResponse {

    private String id;

    private String label;
}
