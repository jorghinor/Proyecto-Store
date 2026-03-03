package com.gutti.store.domain;

import lombok.Getter;

/**
 * @author Ivan Alban
 */
public enum CategoryType {

    MALE("Male"),
    FEMALE("Female"),
    UNISEX("Unisex");

    @Getter
    private String label;

    CategoryType(String label) {
        this.label = label;
    }
}