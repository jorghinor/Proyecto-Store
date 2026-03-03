package com.gutti.store.views;

import com.gutti.store.domain.Organization;
import com.gutti.store.services.OrganizationService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "admin/organizations", layout = MainLayout.class)
@PageTitle("Admin: Empresas | Gutti Store")
@RolesAllowed("ROLE_SUPER_ADMIN")
@CssImport("./styles/organizations-view.css")
public class OrganizationsAdminView extends VerticalLayout {

    private final Grid<Organization> grid = new Grid<>(Organization.class, false);
    private final TextField filterText = new TextField();
    private final OrganizationForm form;

    private final OrganizationService organizationService;
    private ConfigurableFilterDataProvider<Organization, Void, String> dataProvider;

    public OrganizationsAdminView(OrganizationService organizationService) {
        this.organizationService = organizationService;
        addClassName("organizations-view");
        setSizeFull();

        this.form = new OrganizationForm();
        form.setWidth("25em");
        form.addListener(OrganizationForm.SaveEvent.class, this::saveOrganization);
        form.addListener(OrganizationForm.DeleteEvent.class, this::deleteOrganization);
        form.addListener(OrganizationForm.CloseEvent.class, e -> closeEditor());

        Component content = getContent();
        setFlexGrow(1, content);
        add(getToolbar(), content);
        configureGrid();
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

    private void configureGrid() {
        grid.addClassName("styled-grid");
        grid.setSizeFull();

        dataProvider = new CallbackDataProvider<Organization, String>(
                (Query<Organization, String> query) -> organizationService.fetchPageForAdmin(
                        query.getPage(),
                        query.getPageSize(),
                        query.getFilter().orElse(null)
                ).stream(),
                (Query<Organization, String> query) -> organizationService.countForAdmin(query.getFilter().orElse(null))
        ).withConfigurableFilter();

        grid.setDataProvider(dataProvider);

        grid.addColumn(Organization::getName).setHeader("Nombre").setSortable(true);
        grid.addColumn(Organization::getPhone).setHeader("Teléfono");
        grid.addColumn(Organization::getEmail).setHeader("Email");

        grid.addComponentColumn(organization -> {
            Button editButton = new Button("Editar", new Icon(VaadinIcon.PENCIL));
            editButton.addClassName("grid-action-button");
            editButton.addClickListener(e -> editOrganization(organization));

            Button deleteButton = new Button("Eliminar", new Icon(VaadinIcon.TRASH));
            deleteButton.addClassName("grid-action-button");
            deleteButton.addClassName("delete");
            deleteButton.addClickListener(e -> confirmDelete(organization));

            HorizontalLayout actions = new HorizontalLayout(editButton, deleteButton);
            actions.setSpacing(false);
            actions.getThemeList().add("spacing-s");
            return actions;
        }).setHeader("Acciones");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event -> editOrganization(event.getValue()));
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filtrar por nombre...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> dataProvider.setFilter(e.getValue()));

        Button addOrganizationButton = new Button("Nueva Empresa");
        addOrganizationButton.addClassName("add-button");
        addOrganizationButton.addClickListener(click -> addOrganization());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addOrganizationButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void saveOrganization(OrganizationForm.SaveEvent event) {
        organizationService.save(event.getOrganization());
        dataProvider.refreshAll();
        closeEditor();
    }

    private void deleteOrganization(OrganizationForm.DeleteEvent event) {
        confirmDelete(event.getOrganization());
    }

    private void confirmDelete(Organization organization) {
        if (organization == null) return;
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Eliminar Empresa");
        dialog.setText("¿Estás seguro de que quieres eliminar '" + organization.getName() + "'?");
        dialog.setConfirmText("Eliminar");
        dialog.setConfirmButtonTheme("error primary");
        dialog.setCancelText("Cancelar");

        dialog.addConfirmListener(e -> {
            organizationService.delete(organization);
            dataProvider.refreshAll();
            closeEditor();
        });

        dialog.open();
    }

    private void addOrganization() {
        grid.asSingleSelect().clear();
        editOrganization(new Organization());
    }

    public void editOrganization(Organization organization) {
        if (organization == null) {
            closeEditor();
        } else {
            form.setOrganization(organization);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setOrganization(null);
        form.setVisible(false);
        removeClassName("editing");
    }
}
