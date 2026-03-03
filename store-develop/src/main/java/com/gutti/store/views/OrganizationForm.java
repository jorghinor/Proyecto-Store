package com.gutti.store.views;

import com.gutti.store.domain.Organization;
import com.vaadin.flow.component.Component;
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
import com.vaadin.flow.shared.Registration;
import lombok.Getter;

// Se revierte a FormLayout, ya no es un Dialog.
public class OrganizationForm extends FormLayout {

    TextField name = new TextField("Nombre");
    TextField logoUrl = new TextField("URL del Logo");
    TextField phone = new TextField("Teléfono");
    TextField email = new TextField("Email");
    TextField address = new TextField("Dirección");

    Button save = new Button("Guardar");
    Button delete = new Button("Eliminar");
    Button close = new Button("Cancelar");

    Binder<Organization> binder = new BeanValidationBinder<>(Organization.class);

    public OrganizationForm() {
        addClassName("organization-form");
        binder.bindInstanceFields(this);

        // Se añaden los campos y los botones directamente al FormLayout.
        add(name, logoUrl, phone, email, address, createButtonsLayout());
    }

    public void setOrganization(Organization organization) {
        binder.setBean(organization);
        // El botón de eliminar solo es visible si la organización ya existe.
        delete.setVisible(organization != null && organization.getId() != null);
    }

    private Component createButtonsLayout() {
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
        if (binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }

    // --- Eventos (sin cambios) ---
    @Getter
    public static abstract class OrganizationFormEvent extends ComponentEvent<OrganizationForm> {
        private final Organization organization;

        protected OrganizationFormEvent(OrganizationForm source, Organization organization) {
            super(source, false);
            this.organization = organization;
        }
    }

    public static class SaveEvent extends OrganizationFormEvent {
        SaveEvent(OrganizationForm source, Organization organization) {
            super(source, organization);
        }
    }

    public static class DeleteEvent extends OrganizationFormEvent {
        DeleteEvent(OrganizationForm source, Organization organization) {
            super(source, organization);
        }
    }

    public static class CloseEvent extends OrganizationFormEvent {
        CloseEvent(OrganizationForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}