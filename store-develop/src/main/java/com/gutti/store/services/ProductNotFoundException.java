package com.gutti.store.services;

import org.springframework.http.HttpStatus;

import com.gutti.store.exception.ClientException;

/**
 * @author Jorge Quispe
 */
public class ProductNotFoundException extends ClientException {

    private final Long id;

    public ProductNotFoundException(Long id) {
        this.id = id;
    }

    @Override
    public String message() {
        return String.format("Product with id '%d' not found", id);
    }

    @Override
    public HttpStatus errorCode() {
        return HttpStatus.NOT_FOUND;
    }
}