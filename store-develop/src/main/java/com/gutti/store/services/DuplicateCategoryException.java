package com.gutti.store.services;

import com.gutti.store.exception.ClientException;
import org.springframework.http.HttpStatus;

/**
 * @author Ivan Alban
 */
class DuplicateCategoryException extends ClientException {

    private final String label;

    private final String categoryTypeId;

    public DuplicateCategoryException(String label, String categoryTypeId) {
        this.label = label;
        this.categoryTypeId = categoryTypeId;
    }

    @Override
    public String message() {
        return "Already exists a Category for " + label + ", " + categoryTypeId;
    }

    @Override
    public HttpStatus errorCode() {
        return HttpStatus.CONFLICT;
    }
}
