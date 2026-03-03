package com.gutti.store.services;

import org.springframework.http.HttpStatus;

import com.gutti.store.exception.ClientException;

/**
 * @author Jorge Quispe
 */
public class DuplicateProductException extends ClientException {

    private final String name;

    public DuplicateProductException(String name) {
        this.name = name;
    }

    @Override
    public String message() {
        return String.format("Product with name '%s' already exists", name);
    }

    @Override
    public HttpStatus errorCode() {
        return HttpStatus.NOT_FOUND;
    }
}
