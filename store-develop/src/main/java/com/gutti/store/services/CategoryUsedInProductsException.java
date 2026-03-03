package com.gutti.store.services;

import com.gutti.store.exception.ClientException;
import org.springframework.http.HttpStatus;

/**
 * @author Jorge Quispe
 */
public class CategoryUsedInProductsException extends ClientException {

    private final String message;

    public CategoryUsedInProductsException(String label) {
        this.message = String.format("The category is used in products and cannot be deleted", label);
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public HttpStatus errorCode() {
        return HttpStatus.CONFLICT;
    }
}
