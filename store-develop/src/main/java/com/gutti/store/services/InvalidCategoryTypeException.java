package com.gutti.store.services;

import com.gutti.store.exception.ClientException;
import org.springframework.http.HttpStatus;

/**
 * @author Ivan Alban
 */
class InvalidCategoryTypeException extends ClientException {

    private final String categoryTypeId;

    public InvalidCategoryTypeException(String categoryTypeId) {
        this.categoryTypeId = categoryTypeId;
    }

    @Override
    public String message() {
        return "The " + categoryTypeId + " is not valid for this operation";
    }

    @Override
    public HttpStatus errorCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
