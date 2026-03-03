package com.gutti.store.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Data
@Entity
@Table(name = "product_category") // Asegúrate de que este sea el nombre correcto de tu tabla
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    // --- RELACIÓN CORREGIDA ---
    // Reemplazamos el 'productId' por un objeto 'Product' completo.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false) // Le dice a JPA que esta es la columna de la clave foránea
    @ToString.Exclude // Evita bucles infinitos en logs
    private Product product; // Este es el campo al que se refiere mappedBy="product" en Product.java

    // --- RELACIÓN CORREGIDA ---
    // Reemplazamos el 'categoryId' por un objeto 'Category' completo.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false) // Le dice a JPA que esta es la columna de la clave foránea
    @ToString.Exclude
    private Category category;

    // Mantenemos organizationId como UUID, ya que es consistente con el resto del diseño.
    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;
}