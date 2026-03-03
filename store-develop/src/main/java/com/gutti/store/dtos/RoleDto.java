package com.gutti.store.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor // <-- LA ANOTACIÓN CLAVE QUE FALTABA
public class RoleDto {
    private String id;
    private String name;
}