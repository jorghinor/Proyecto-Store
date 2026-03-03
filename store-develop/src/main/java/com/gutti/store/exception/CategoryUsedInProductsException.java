package com.gutti.store.exception;

public class CategoryUsedInProductsException extends RuntimeException {
    public CategoryUsedInProductsException(String categoryLabel) {
        super("Cannot delete category '" + categoryLabel + "' because it is in use by products.");
    }
}