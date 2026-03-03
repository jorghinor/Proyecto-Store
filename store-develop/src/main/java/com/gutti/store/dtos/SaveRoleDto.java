package com.gutti.store.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor // <-- LA ANOTACIÓN CLAVE QUE FALTABA
public class SaveRoleDto {
    //private String id;
    private String name;
}
