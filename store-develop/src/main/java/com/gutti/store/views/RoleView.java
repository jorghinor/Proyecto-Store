package com.gutti.store.views;

import com.gutti.store.dtos.RoleDto;
import com.gutti.store.dtos.SaveRoleDto;
import com.gutti.store.exception.DuplicateResourceException;
import com.gutti.store.exception.ResourceInUseException;
import com.gutti.store.services.RoleService;
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

@PermitAll
@Route(value = "roles", layout = MainLayout.class)
@PageTitle("Roles | Gutti Store")
public class RoleView extends VerticalLayout {

    Grid<RoleDto> grid = new Grid<>(RoleDto.class);
    TextField filterText = new TextField();
    RoleForm form;

    private final RoleService roleService;
    private ConfigurableFilterDataProvider<RoleDto, Void, String> dataProvider;

    public RoleView(RoleService roleService) {
        this.roleService = roleService;
        addClassName("role-view");
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
        form = new RoleForm();
        form.setWidth("25em");
        form.addSaveListener(this::saveRole);
        form.addDeleteListener(this::deleteRole);
        form.addCloseListener(e -> closeEditor());
    }

    private void saveRole(RoleForm.SaveEvent event) {
        SaveRoleDto saveDto = new SaveRoleDto(event.getRole().getName());
        try {
            if (event.getRole().getId() == null) {
                roleService.save(saveDto);
            } else {
                roleService.update(event.getRole().getId(), saveDto);
            }
            dataProvider.refreshAll();
            closeEditor();
        } catch (DuplicateResourceException e) {
            Notification.show(e.getMessage(), 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void deleteRole(RoleForm.DeleteEvent event) {
        try {
            roleService.delete(event.getRole().getId());
            dataProvider.refreshAll();
            closeEditor();
        } catch (ResourceInUseException e) {
            Notification.show(e.getMessage(), 5000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void configureGrid() {
        grid.addClassNames("role-grid");
        grid.addClassNames("styled-grid");
        grid.setSizeFull();
        grid.setPageSize(15);
        grid.setColumns("name");
        grid.getColumnByKey("name").setHeader("Nombre");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.addComponentColumn(role -> {
            Button editButton = new Button("Editar", new Icon(VaadinIcon.PENCIL));
            editButton.addClassName("grid-action-button");
            editButton.addClickListener(e -> editRole(role));

            Button deleteButton = new Button("Eliminar", new Icon(VaadinIcon.TRASH));
            deleteButton.addClassName("grid-action-button");
            deleteButton.addClassName("delete");
            deleteButton.addClickListener(e -> confirmDelete(role));

            HorizontalLayout buttons = new HorizontalLayout(editButton, deleteButton);
            buttons.setSpacing(false);
            buttons.getThemeList().add("spacing-s");
            return buttons;
        }).setHeader("Acciones");

        DataProvider<RoleDto, String> baseDataProvider = DataProvider.fromFilteringCallbacks(
                query -> roleService.fetchPage(query.getFilter().orElse(null), PageRequest.of(query.getPage(), query.getPageSize())).stream(),
                query -> (int) roleService.count(query.getFilter().orElse(null))
        );

        dataProvider = baseDataProvider.withConfigurableFilter();
        grid.setDataProvider(dataProvider);
    }

    private void confirmDelete(RoleDto role) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Eliminar Rol '" + role.getName() + "'?");
        dialog.setText("¿Estás seguro de que quieres eliminar este rol permanentemente?");
        dialog.setConfirmText("Eliminar");
        dialog.setCancelText("Cancelar");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(event -> deleteRole(new RoleForm.DeleteEvent(form, role)));
        dialog.open();
    }

    private Component getToolbar() {
        filterText.setPlaceholder("Filtrar por nombre...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> dataProvider.setFilter(e.getValue()));

        Button addRoleButton = new Button("Añadir Rol");
        addRoleButton.addClassName("add-button");
        addRoleButton.addClickListener(click -> addRole());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addRoleButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void addRole() {
        grid.asSingleSelect().clear();
        editRole(new RoleDto());
    }

    public void editRole(RoleDto role) {
        if (role == null) {
            closeEditor();
        } else {
            form.setRole(role);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setRole(null);
        form.setVisible(false);
        removeClassName("editing");
    }
}