package com.gutti.store.exception;

public class DuplicateCategoryException extends RuntimeException {
    public DuplicateCategoryException(String label, String categoryTypeId) {
        super("A category with label '" + label + "' and type '" + categoryTypeId + "' already exists.");
    }
}