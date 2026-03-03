package com.gutti.store.views;

import com.gutti.store.domain.ItemProperty;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import lombok.Getter;

/**
 * Formulario para crear y editar una Propiedad de Item (ItemProperty).
 */
public class ItemPropertyForm extends FormLayout {

    Binder<ItemProperty> binder = new BeanValidationBinder<>(ItemProperty.class);

    TextField label = new TextField("Nombre de la Propiedad");

    Button save = new Button("Guardar");
    Button delete = new Button("Eliminar");
    Button close = new Button("Cancelar");

    public ItemPropertyForm() {
        addClassName("item-property-form");
        binder.bindInstanceFields(this);

        add(label, createButtonsLayout());
    }

    public void setItemProperty(ItemProperty itemProperty) {
        binder.setBean(itemProperty);
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
    public static abstract class ItemPropertyFormEvent extends ComponentEvent<ItemPropertyForm> {
        private final ItemProperty itemProperty;

        protected ItemPropertyFormEvent(ItemPropertyForm source, ItemProperty itemProperty) {
            super(source, false);
            this.itemProperty = itemProperty;
        }
    }

    public static class SaveEvent extends ItemPropertyFormEvent {
        SaveEvent(ItemPropertyForm source, ItemProperty itemProperty) {
            super(source, itemProperty);
        }
    }

    public static class DeleteEvent extends ItemPropertyFormEvent {
        DeleteEvent(ItemPropertyForm source, ItemProperty itemProperty) {
            super(source, itemProperty);
        }
    }

    public static class CloseEvent extends ItemPropertyFormEvent {
        CloseEvent(ItemPropertyForm source) {
            super(source, null);
        }
    }

    public void addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        addListener(DeleteEvent.class, listener);
    }

    public void addSaveListener(ComponentEventListener<SaveEvent> listener) {
        addListener(SaveEvent.class, listener);
    }

    public void addCloseListener(ComponentEventListener<CloseEvent> listener) {
        addListener(CloseEvent.class, listener);
    }
}