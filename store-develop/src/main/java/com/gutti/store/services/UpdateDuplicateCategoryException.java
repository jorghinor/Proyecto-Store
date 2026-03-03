package com.gutti.store.services;

import com.gutti.store.exception.ClientException;
import org.springframework.http.HttpStatus;

public class UpdateDuplicateCategoryException extends ClientException {

    private final String label;

    public UpdateDuplicateCategoryException(String label) {
        this.label = label;
    }

    @Override
    public String message() {
        return String.format("A category already exists %s", label);
    }

    @Override
    public HttpStatus errorCode() {
        return HttpStatus.CONFLICT;
    }
}
