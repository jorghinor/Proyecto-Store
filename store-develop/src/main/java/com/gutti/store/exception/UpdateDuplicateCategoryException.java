package com.gutti.store.exception;

public class UpdateDuplicateCategoryException extends RuntimeException {
    public UpdateDuplicateCategoryException(String label) {
        super("Another category with the label '" + label + "' already exists. Cannot update.");
    }
}