package com.gutti.store.views;

import com.gutti.store.dtos.RoleDto; // Importamos RoleDto
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
import com.vaadin.flow.shared.Registration;
import lombok.Getter;

/**
 * Formulario para crear y editar un Rol.
 */
public class RoleForm extends FormLayout {

    // Corregido: El Binder ahora trabaja con RoleDto
    Binder<RoleDto> binder = new BeanValidationBinder<>(RoleDto.class);

    TextField name = new TextField("Nombre del Rol");

    Button save = new Button("Guardar");
    Button delete = new Button("Eliminar");
    Button close = new Button("Cancelar");

    public RoleForm() {
        addClassName("role-form");
        binder.bindInstanceFields(this);
        add(name, createButtonsLayout());
    }

    // Corregido: El método ahora acepta un RoleDto
    public void setRole(RoleDto role) {
        binder.setBean(role);
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

    // --- Sistema de Eventos (Corregido para usar RoleDto) ---
    @Getter
    public static abstract class RoleFormEvent extends ComponentEvent<RoleForm> {
        private final RoleDto role;

        protected RoleFormEvent(RoleForm source, RoleDto role) {
            super(source, false);
            this.role = role;
        }
    }

    public static class SaveEvent extends RoleFormEvent {
        SaveEvent(RoleForm source, RoleDto role) {
            super(source, role);
        }
    }

    public static class DeleteEvent extends RoleFormEvent {
        DeleteEvent(RoleForm source, RoleDto role) {
            super(source, role);
        }
    }

    public static class CloseEvent extends RoleFormEvent {
        CloseEvent(RoleForm source) {
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