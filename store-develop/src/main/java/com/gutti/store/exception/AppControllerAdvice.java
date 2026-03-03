package com.gutti.store.exception;

import com.gutti.store.services.CategoryUsedInProductsException;
import com.gutti.store.services.ProductHasCategoriesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;

/**
 * @author Ivan Alban
 */
@ControllerAdvice
public class AppControllerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppControllerAdvice.class);

    @ExceptionHandler(ClientException.class)
    public ResponseEntity<ErrorResponse> handleClientException(ClientException exception) {
        return ResponseEntity.status(exception.errorCode()).body(ErrorResponse.builder()
                .code(exception.errorCode().value())
                .message(exception.message())
                .build());
    }

    @ExceptionHandler(CategoryUsedInProductsException.class)
    public ResponseEntity<ErrorResponse> handleCategoryUsedInProductsException(CategoryUsedInProductsException exception) {
        return ResponseEntity.status(exception.errorCode()).body(ErrorResponse.builder()
                .code(exception.errorCode().value())
                .message(exception.message())
                .build());
    }

    @ExceptionHandler(ProductHasCategoriesException.class)
    public ResponseEntity<ErrorResponse> handleProductHasCategoriesException(ProductHasCategoriesException exception) {
        return ResponseEntity.status(exception.errorCode()).body(ErrorResponse.builder()
                .code(exception.errorCode().value())
                .message(exception.message())
                .build());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .message(exception.getMessage())
                .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception exception) {
        LOGGER.error("Unhandled exception", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Unexpected server error")
                .build());
    }
}
