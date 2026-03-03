package com.gutti.store.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.TenantId;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "item_property")
public class ItemProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String label;

    @OneToMany(mappedBy = "itemProperty", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPropertyValue> values;

    @TenantId
    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    private Boolean deleted = false;

    // Constructor de conveniencia para crear nuevas propiedades
    public ItemProperty(String label, UUID organizationId) {
        this.label = label;
        this.organizationId = organizationId;
    }
}
