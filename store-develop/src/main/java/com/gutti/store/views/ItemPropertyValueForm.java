package com.gutti.store.views;

import com.gutti.store.domain.ItemProperty;
import com.gutti.store.domain.ItemPropertyValue;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;

import java.util.List;

public class ItemPropertyValueForm extends FormLayout {

    Binder<ItemPropertyValue> binder = new BeanValidationBinder<>(ItemPropertyValue.class);

    ComboBox<ItemProperty> itemProperty = new ComboBox<>("Propiedad Padre");
    TextField value = new TextField("Valor de la Propiedad"); // Corregido

    Button save = new Button("Guardar");
    Button delete = new Button("Eliminar");
    Button close = new Button("Cancelar");

    public ItemPropertyValueForm(List<ItemProperty> properties) {
        addClassName("item-property-value-form");
        binder.bindInstanceFields(this);

        itemProperty.setItems(properties);
        itemProperty.setItemLabelGenerator(ItemProperty::getLabel);

        add(itemProperty, value, createButtonsLayout()); // Corregido
    }

    public void setItemPropertyValue(ItemPropertyValue itemValue) {
        binder.setBean(itemValue);
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
            e.printStackTrace();
        }
    }

    // --- Sistema de Eventos ---
    @Getter
    public static abstract class ItemPropertyValueFormEvent extends ComponentEvent<ItemPropertyValueForm> {
        private final ItemPropertyValue itemPropertyValue;

        protected ItemPropertyValueFormEvent(ItemPropertyValueForm source, ItemPropertyValue itemPropertyValue) {
            super(source, false);
            this.itemPropertyValue = itemPropertyValue;
        }
    }

    public static class SaveEvent extends ItemPropertyValueFormEvent {
        SaveEvent(ItemPropertyValueForm source, ItemPropertyValue itemPropertyValue) {
            super(source, itemPropertyValue);
        }
    }

    public static class DeleteEvent extends ItemPropertyValueFormEvent {
        DeleteEvent(ItemPropertyValueForm source, ItemPropertyValue itemPropertyValue) {
            super(source, itemPropertyValue);
        }
    }

    public static class CloseEvent extends ItemPropertyValueFormEvent {
        CloseEvent(ItemPropertyValueForm source) {
            super(source, null);
        }
    }

    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        return addListener(DeleteEvent.class, listener);
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }
}