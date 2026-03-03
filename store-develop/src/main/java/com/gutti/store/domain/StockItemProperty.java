package com.gutti.store.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "stock_item_property")
public class StockItemProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_item_id", nullable = false)
    private StockItem stockItem;

    @ManyToOne(fetch = FetchType.EAGER) // MODIFICADO: Cambiado a EAGER
    @JoinColumn(name = "item_property_id", nullable = false)
    private ItemProperty itemProperty;

    @ManyToOne
    @JoinColumn(name = "item_property_value_id", nullable = false)
    private ItemPropertyValue itemPropertyValue;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;
}
