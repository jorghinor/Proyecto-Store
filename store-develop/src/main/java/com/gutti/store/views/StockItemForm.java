package com.gutti.store.views;

import com.gutti.store.domain.Category;
import com.gutti.store.domain.Product;
import com.gutti.store.domain.StockItem;
import com.gutti.store.domain.StockItemDetail;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StockItemForm extends FormLayout {

    Binder<StockItem> binder = new BeanValidationBinder<>(StockItem.class);

    ComboBox<Category> category = new ComboBox<>("Categoría");
    ComboBox<Product> product = new ComboBox<>("Producto");
    IntegerField quantity = new IntegerField("Cantidad");
    NumberField unitPrice = new NumberField("Precio Unitario");

    Button save = new Button("Guardar");
    Button delete = new Button("Eliminar");
    Button close = new Button("Cancelar");

    private final List<Product> allProducts;

    public StockItemForm(List<Product> allProducts, List<Category> allCategories) {
        this.allProducts = allProducts; // Guardamos la lista completa de productos
        addClassName("stock-item-form");

        // --- LÓGICA EN CASCADA CORREGIDA ---

        // 1. Llenamos el ComboBox de Categorías con las entidades Category
        category.setItems(allCategories);
        category.setItemLabelGenerator(Category::getLabel);

        // 2. El ComboBox de Productos empieza vacío y deshabilitado
        product.setEnabled(false);
        product.setItems(Collections.emptyList());
        product.setItemLabelGenerator(Product::getName);

        // 3. Añadimos un listener que reacciona al cambio de categoría
        category.addValueChangeListener(event -> {
            Category selectedCategory = event.getValue();
            product.clear(); // Limpiamos la selección de producto anterior

            if (selectedCategory != null) {
                // Filtramos la lista de TODOS los productos para mostrar solo los de la categoría seleccionada
                List<Product> filteredProducts = this.allProducts.stream()
                        .filter(p -> p.getCategories().stream()
                                .anyMatch(pc -> pc.getCategory().getId().equals(selectedCategory.getId())))
                        .collect(Collectors.toList());

                product.setItems(filteredProducts);
                product.setEnabled(true);
            } else {
                // Si no hay categoría seleccionada, vaciamos y deshabilitamos el ComboBox de productos
                product.setItems(Collections.emptyList());
                product.setEnabled(false);
            }
        });

        // Enlazamos los campos al binder
        // OJO: El binder sigue enlazado a 'product' y 'productCategory' del StockItem,
        // pero la UI usa el combo 'category' para controlar el flujo.

        // Este binding es un poco más complejo ahora.
        // Cuando se selecciona un producto, debemos encontrar el ProductCategory correspondiente.
        binder.forField(product).bind(StockItem::getProduct, (stockItem, selectedProduct) -> {
            if (selectedProduct != null && category.getValue() != null) {
                selectedProduct.getCategories().stream()
                        .filter(pc -> pc.getCategory().getId().equals(category.getValue().getId()))
                        .findFirst()
                        .ifPresent(stockItem::setProductCategory);
            }
            stockItem.setProduct(selectedProduct);
        });


        // Binding manual para quantity y unitPrice
        binder.forField(quantity)
                .bind(
                        stockItem -> stockItem.getDetails() != null && !stockItem.getDetails().isEmpty() ? stockItem.getDetails().getFirst().getQuantity() : 0,
                        (stockItem, qty) -> {
                            if (stockItem.getDetails() == null) stockItem.setDetails(new ArrayList<>());
                            if (stockItem.getDetails().isEmpty()) stockItem.getDetails().add(new StockItemDetail());
                            stockItem.getDetails().getFirst().setQuantity(qty);
                        }
                );

        binder.forField(unitPrice)
                .bind(
                        stockItem -> {
                            if (stockItem.getDetails() != null && !stockItem.getDetails().isEmpty() && stockItem.getDetails().getFirst().getUnitPrice() != null) {
                                return stockItem.getDetails().getFirst().getUnitPrice().doubleValue();
                            }
                            return 0.0;
                        },
                        (stockItem, price) -> {
                            if (stockItem.getDetails() == null) stockItem.setDetails(new ArrayList<>());
                            if (stockItem.getDetails().isEmpty()) stockItem.getDetails().add(new StockItemDetail());
                            stockItem.getDetails().getFirst().setUnitPrice(price != null ? BigDecimal.valueOf(price) : BigDecimal.ZERO);
                        }
                );

        add(category, product, quantity, unitPrice, createButtonsLayout());
    }

    public void setStockItem(StockItem stockItem) {
        // --- LÓGICA CORREGIDA PARA EDITAR ---
        // 1. Guardamos la referencia al bean que estamos editando.
        binder.setBean(stockItem);

        // Si estamos editando un item, pre-cargamos los ComboBox
        if (stockItem != null && stockItem.getProductCategory() != null) {
            // 2. Usamos readBean() para que el Binder rellene los campos del formulario
            // a partir del objeto. Esto es más robusto que usar setValue() manualmente.
            binder.readBean(stockItem);

            // 3. Disparamos manualmente el evento de cambio de categoría para poblar la lista de productos.
            category.setValue(stockItem.getProductCategory().getCategory());
        } else {
            // Si es un item nuevo, reseteamos los combos
            category.clear();
            product.clear();
            product.setEnabled(false);
            // Y limpiamos el binder para que no tenga datos viejos.
            binder.readBean(new StockItem());
        }
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(binder.getBean());
            fireEvent(new SaveEvent(this, binder.getBean()));
        } catch (ValidationException e) {
            // La validación del binder ya muestra los errores
        }
    }

    // --- Sistema de Eventos ---
    @Getter
    public static abstract class StockItemFormEvent extends ComponentEvent<StockItemForm> {
        private final StockItem stockItem;

        protected StockItemFormEvent(StockItemForm source, StockItem stockItem) {
            super(source, false);
            this.stockItem = stockItem;
        }
    }

    public static class SaveEvent extends StockItemFormEvent {
        SaveEvent(StockItemForm source, StockItem stockItem) {
            super(source, stockItem);
        }
    }

    public static class DeleteEvent extends StockItemFormEvent {
        DeleteEvent(StockItemForm source, StockItem stockItem) {
            super(source, stockItem);
        }
    }

    public static class CloseEvent extends StockItemFormEvent {
        CloseEvent(StockItemForm source) {
            super(source, null);
        }
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        return addListener(DeleteEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }
}