package com.gutti.store.exception;

import lombok.*;

/**
 * @author Ivan Alban
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private String message;

    private Integer code;
}
