package com.gutti.store.services;

import com.gutti.store.exception.ClientException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Jorge Quispe
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ProductAlreadyHasCategoryException extends ClientException {
    public ProductAlreadyHasCategoryException() {
    }

    @Override
    public HttpStatus errorCode() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String message() {
        return "Product already has this category";
    }
}