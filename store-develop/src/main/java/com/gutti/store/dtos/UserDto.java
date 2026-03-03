package com.gutti.store.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor // <-- LA ANOTACIÓN CLAVE QUE FALTABA
public class UserDto {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private List<String> roles;
}
