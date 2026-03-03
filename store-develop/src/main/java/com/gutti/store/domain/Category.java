package com.gutti.store.domain;

import jakarta.persistence.*;
import lombok.*;
/*import org.hibernate.annotations.TenantId;*/

//import java.util.UUID;

/**
 * @author Ivan Alban
 */
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "label", nullable = false)
    private String label;

    @Enumerated(EnumType.STRING)
    @Column(name = "category_type", nullable = false)
    private CategoryType categoryType;

    @Column(name = "deleted")
    private Boolean deleted;

    // --- RELACIÓN CORREGIDA ---
    // Ahora es un objeto, no solo un ID.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id") // Le dice a JPA que esta es la columna de la clave foránea
    @ToString.Exclude
    private Organization organization;

    /* @TenantId
    //@Column(name = "organization_id", nullable = false)
    //private UUID organizationId;*/
}
