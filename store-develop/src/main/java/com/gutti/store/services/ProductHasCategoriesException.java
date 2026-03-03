package com.gutti.store.services;

import com.gutti.store.exception.ClientException;
import org.springframework.http.HttpStatus;

/**
 * @author Jorge Quispe
 */
public class ProductHasCategoriesException extends ClientException {

    private final String message;

    public ProductHasCategoriesException(String productName) {
        this.message = String.format("The product contains categories and cannot be deleted", productName);
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