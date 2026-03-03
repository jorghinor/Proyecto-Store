package com.gutti.store.exception;

import org.springframework.http.HttpStatus;

/**
 * @author Ivan Alban
 */
public abstract class ServerException extends RuntimeException {

    public abstract HttpStatus errorCode();
}
