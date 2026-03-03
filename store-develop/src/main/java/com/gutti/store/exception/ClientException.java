package com.gutti.store.exception;

import org.springframework.http.HttpStatus;

/**
 * @author Ivan Alban
 */
public abstract class ClientException extends RuntimeException {

    public abstract String message();

    public abstract HttpStatus errorCode();
}
