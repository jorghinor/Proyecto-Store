package com.gutti.store.views;

import com.gutti.store.domain.Product;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;

import java.math.BigDecimal;

public class ProductForm extends FormLayout {

    Binder<Product> binder = new BeanValidationBinder<>(Product.class);

    TextField name = new TextField("Nombre del Producto");
    TextField brand = new TextField("Marca");
    TextField imageUrl = new TextField("URL de la Imagen");
    NumberField price = new NumberField("Precio"); // Nuevo campo
    NumberField discountPercentage = new NumberField("Descuento (%)");
    Checkbox onPromotion = new Checkbox("En Promoción");
    Checkbox newArrival = new Checkbox("Nuevo Ingreso");

    Button save = new Button("Guardar");
    Button delete = new Button("Eliminar");
    Button close = new Button("Cancelar");

    public ProductForm() {
        addClassName("product-form");

        // Configuración del campo de precio
        price.setSuffixComponent(new com.vaadin.flow.component.html.Span("$"));
        price.setMin(0);

        // --- ESTA ES LA SOLUCIÓN ---
        // Eliminamos binder.bindInstanceFields(this) y enlazamos cada campo manualmente.

        // Campos de texto simples (String)
        binder.forField(name).bind(Product::getName, Product::setName);
        binder.forField(brand).bind(Product::getBrand, Product::setBrand);
        binder.forField(imageUrl).bind(Product::getImageUrl, Product::setImageUrl);

        // Checkboxes (boolean)
        binder.forField(onPromotion).bind(Product::isOnPromotion, Product::setOnPromotion);
        binder.forField(newArrival).bind(Product::isNewArrival, Product::setNewArrival);

        // Campo numérico con conversión manual (Double <-> BigDecimal) para Precio
        binder.forField(price)
                .withConverter(
                        doubleValue -> doubleValue != null ? BigDecimal.valueOf(doubleValue) : null,
                        bigDecimalValue -> {
                            if (bigDecimalValue == null) {
                                return null;
                            }
                            return bigDecimalValue.doubleValue();
                        },
                        "Por favor, ingrese un precio válido."
                )
                .bind(Product::getPrice, Product::setPrice);

        // Campo numérico con conversión manual (Double <-> BigDecimal) para Descuento
        binder.forField(discountPercentage)
                .withConverter(
                        doubleValue -> doubleValue != null ? BigDecimal.valueOf(doubleValue) : null,
                        bigDecimalValue -> {
                            if (bigDecimalValue == null) {
                                return null;
                            }
                            return bigDecimalValue.doubleValue() == 0.0 ? null : bigDecimalValue.doubleValue();
                        },
                        "Por favor, ingrese un número válido."
                )
                .bind(Product::getDiscountPercentage, Product::setDiscountPercentage);


        // Agrupamos los checkboxes para un mejor diseño
        HorizontalLayout flagsLayout = new HorizontalLayout(onPromotion, newArrival);

        add(name, brand, imageUrl, price, discountPercentage, flagsLayout, createButtonsLayout());
    }

    public void setProduct(Product product) {
        binder.setBean(product);
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
    public static abstract class ProductFormEvent extends ComponentEvent<ProductForm> {
        private final Product product;

        protected ProductFormEvent(ProductForm source, Product product) {
            super(source, false);
            this.product = product;
        }
    }

    public static class SaveEvent extends ProductFormEvent {
        SaveEvent(ProductForm source, Product product) {
            super(source, product);
        }
    }

    public static class DeleteEvent extends ProductFormEvent {
        DeleteEvent(ProductForm source, Product product) {
            super(source, product);
        }
    }

    public static class CloseEvent extends ProductFormEvent {
        CloseEvent(ProductForm source) {
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
