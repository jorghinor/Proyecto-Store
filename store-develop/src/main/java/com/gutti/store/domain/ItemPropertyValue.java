package com.gutti.store.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.TenantId;

import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "item_property_value")
public class ItemPropertyValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // --- CORRECCIÓN CLAVE ---
    // El campo en Java se llama 'value', pero se mapea a la columna 'item_property_value'.
    @Column(name = "item_property_value", nullable = false)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_property_id", nullable = false)
    private ItemProperty itemProperty;

    @TenantId
    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    private Boolean deleted = false;
}
