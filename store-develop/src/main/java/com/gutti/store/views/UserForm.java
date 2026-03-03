package com.gutti.store.views;

import com.gutti.store.dtos.RoleDto;
import com.gutti.store.dtos.UserDto;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Formulario para crear y editar un Usuario.
 */
public class UserForm extends FormLayout {

    Binder<UserDto> binder = new BeanValidationBinder<>(UserDto.class);

    TextField firstName = new TextField("Nombre");
    TextField lastName = new TextField("Apellido");
    EmailField email = new EmailField("Email");
    PasswordField password = new PasswordField("Contraseña");
    MultiSelectComboBox<RoleDto> roles = new MultiSelectComboBox<>("Roles");

    Button save = new Button("Guardar");
    Button delete = new Button("Eliminar");
    Button close = new Button("Cancelar");

    private final List<RoleDto> allRoles;

    public UserForm(List<RoleDto> allRoles) {
        this.allRoles = allRoles;
        addClassName("user-form");

        // Poblar el ComboBox de roles
        roles.setItems(allRoles);
        roles.setItemLabelGenerator(RoleDto::getName);

        // Enlazar campos al binder (excepto la contraseña)
        binder.forField(firstName).bind("firstName");
        binder.forField(lastName).bind("lastName");
        binder.forField(email).bind("email");

        // Binding personalizado para los roles
        binder.forField(roles)
                .bind(
                        userDto -> allRoles.stream()
                                .filter(roleDto -> userDto.getRoles() != null && userDto.getRoles().contains(roleDto.getId()))
                                .collect(Collectors.toSet()),
                        (userDto, roleDtoSet) -> userDto.setRoles(roleDtoSet.stream()
                                .map(RoleDto::getId)
                                .collect(Collectors.toList()))
                );

        add(firstName, lastName, email, password, roles, createButtonsLayout());
    }

    public void setUser(UserDto user) {
        binder.setBean(user);

        // --- CORRECCIÓN CLAVE AQUÍ ---
        // Si el usuario es null (al cerrar el editor), no hacemos nada más.
        if (user == null) {
            return;
        }

        // La contraseña no se debe mostrar al editar
        password.clear();
        password.setPlaceholder(user.getId() == null ? "" : "Dejar en blanco para no cambiar");
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
            UserDto userDto = binder.getBean();
            // Si es un usuario nuevo y la contraseña está vacía, la validación fallará.
            if (userDto.getId() == null && password.getValue().isEmpty()) {
                // Forzamos la validación para que muestre el error en el campo requerido
                password.setRequired(true);
                password.setErrorMessage("La contraseña es obligatoria para usuarios nuevos");
                password.setInvalid(true);
                return;
            }
            password.setRequired(false); // Reseteamos por si acaso

            binder.writeBean(userDto);
            // Pasamos la contraseña como un parámetro separado en el evento
            fireEvent(new SaveEvent(this, userDto, password.getValue()));

        } catch (ValidationException e) {
            // La validación del binder ya muestra los errores en los campos
        }
    }

    // --- Sistema de Eventos ---
    @Getter
    public static abstract class UserFormEvent extends ComponentEvent<UserForm> {
        private final UserDto user;

        protected UserFormEvent(UserForm source, UserDto user) {
            super(source, false);
            this.user = user;
        }
    }

    @Getter
    public static class SaveEvent extends UserFormEvent {
        private final String password;

        SaveEvent(UserForm source, UserDto user, String password) {
            super(source, user);
            this.password = password;
        }
    }

    public static class DeleteEvent extends UserFormEvent {
        DeleteEvent(UserForm source, UserDto user) {
            super(source, user);
        }
    }

    public static class CloseEvent extends UserFormEvent {
        CloseEvent(UserForm source) {
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