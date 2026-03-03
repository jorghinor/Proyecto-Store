package com.gutti.store.services;

import com.gutti.store.exception.ClientException;
import org.springframework.http.HttpStatus;

/**
 * @author Ivan Alban
 */
class CategoryNotFoundException extends ClientException {

    private final Long categoryId;

    public CategoryNotFoundException(Long categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String message() {
        return "Unable to locate a category for id: " + categoryId;
    }

    @Override
    public HttpStatus errorCode() {
        return HttpStatus.NOT_FOUND;
    }
}
