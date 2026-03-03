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
public class CreateCategoryPayload {

    private String label;

    private String categoryTypeId;
}
