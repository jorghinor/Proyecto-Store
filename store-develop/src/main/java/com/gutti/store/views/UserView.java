package com.gutti.store.views;

import com.gutti.store.dtos.RoleDto;
import com.gutti.store.dtos.SaveUserDto;
import com.gutti.store.dtos.UserDto;
import com.gutti.store.exception.DuplicateResourceException;
import com.gutti.store.services.RoleService;
import com.gutti.store.services.UserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.data.domain.PageRequest;

import java.util.HashSet;
import java.util.List;

@PermitAll
@Route(value = "users", layout = MainLayout.class)
@PageTitle("Usuarios | Gutti Store")
public class UserView extends VerticalLayout {

    Grid<UserDto> grid = new Grid<>(UserDto.class);
    TextField filterText = new TextField();
    UserForm form;

    private final UserService userService;
    private final RoleService roleService;
    private ConfigurableFilterDataProvider<UserDto, Void, String> dataProvider;

    public UserView(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
        addClassName("user-view");
        setSizeFull();

        configureGrid();
        configureForm();

        Component content = getContent();
        setFlexGrow(1, content);
        add(getToolbar(), content);
        closeEditor();
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        List<RoleDto> allRoles = roleService.findAll();
        form = new UserForm(allRoles);
        form.setWidth("25em");
        form.addSaveListener(this::saveUser);
        form.addDeleteListener(this::deleteUser);
        form.addCloseListener(e -> closeEditor());
    }

    private void saveUser(UserForm.SaveEvent event) {
        UserDto userDto = event.getUser();
        String password = event.getPassword();

        SaveUserDto saveDto = new SaveUserDto();
        saveDto.setFirstName(userDto.getFirstName());
        saveDto.setLastName(userDto.getLastName());
        saveDto.setEmail(userDto.getEmail());
        if (userDto.getRoles() != null) {
            saveDto.setRoles(new HashSet<>(userDto.getRoles()));
        }
        if (password != null && !password.isEmpty()) {
            saveDto.setPassword(password);
        }

        try {
            if (userDto.getId() == null) {
                userService.save(saveDto);
            } else {
                userService.update(userDto.getId(), saveDto);
            }
            dataProvider.refreshAll();
            closeEditor();
        } catch (DuplicateResourceException e) {
            Notification.show(e.getMessage(), 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void deleteUser(UserForm.DeleteEvent event) {
        userService.delete(event.getUser().getId());
        dataProvider.refreshAll();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassNames("user-grid");
        grid.addClassNames("styled-grid");
        grid.setSizeFull();
        grid.setPageSize(15);
        grid.setColumns("firstName", "lastName", "email");

        grid.getColumnByKey("firstName").setHeader("Nombre");
        grid.getColumnByKey("lastName").setHeader("Apellido");
        grid.getColumnByKey("email").setHeader("Email");

        grid.addComponentColumn(user -> {
            Button editButton = new Button("Editar", new Icon(VaadinIcon.PENCIL));
            editButton.addClassName("grid-action-button");
            editButton.addClickListener(e -> editUser(user));

            Button deleteButton = new Button("Eliminar", new Icon(VaadinIcon.TRASH));
            deleteButton.addClassName("grid-action-button");
            deleteButton.addClassName("delete");
            deleteButton.addClickListener(e -> confirmDelete(user));

            HorizontalLayout buttons = new HorizontalLayout(editButton, deleteButton);
            buttons.setSpacing(false);
            buttons.getThemeList().add("spacing-s");
            return buttons;
        }).setHeader("Acciones");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        DataProvider<UserDto, String> baseDataProvider = DataProvider.fromFilteringCallbacks(
                query -> {
                    PageRequest pageable = PageRequest.of(query.getPage(), query.getPageSize());
                    return userService.fetchPage(query.getFilter().orElse(null), pageable).stream();
                },
                query -> (int) userService.count(query.getFilter().orElse(null))
        );

        dataProvider = baseDataProvider.withConfigurableFilter();
        grid.setDataProvider(dataProvider);
    }

    private void confirmDelete(UserDto user) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Eliminar Usuario '" + user.getFirstName() + " " + user.getLastName() + "'?");
        dialog.setText("¿Estás seguro de que quieres eliminar este usuario permanentemente?");
        dialog.setConfirmText("Eliminar");
        dialog.setCancelText("Cancelar");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(event -> deleteUser(new UserForm.DeleteEvent(form, user)));
        dialog.open();
    }

    private Component getToolbar() {
        filterText.setPlaceholder("Filtrar por nombre, apellido o email...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> dataProvider.setFilter(e.getValue()));

        Button addUserButton = new Button("Añadir Usuario");
        addUserButton.addClassName("add-button");
        addUserButton.addClickListener(click -> addUser());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addUserButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void addUser() {
        grid.asSingleSelect().clear();
        editUser(new UserDto());
    }

    private void editUser(UserDto user) {
        if (user == null) {
            closeEditor();
            return;
        }

        if (user.getId() == null) {
            form.setUser(user);
            form.setVisible(true);
            addClassName("editing");
        } else {
            userService.findById(user.getId()).ifPresent(fullUserDto -> {
                form.setUser(fullUserDto);
                form.setVisible(true);
                addClassName("editing");
            });
        }
    }

    private void closeEditor() {
        form.setUser(null);
        form.setVisible(false);
        removeClassName("editing");
    }
}