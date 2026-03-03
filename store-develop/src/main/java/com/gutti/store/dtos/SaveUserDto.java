package com.gutti.store.dtos;

import lombok.Data;

import java.util.Set;

@Data
public class SaveUserDto {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Set<String> roles;
}
