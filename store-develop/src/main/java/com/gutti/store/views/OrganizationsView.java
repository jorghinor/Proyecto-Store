package com.gutti.store.views;

import com.gutti.store.configs.AppRequestContext;
import com.gutti.store.domain.Organization;
import com.gutti.store.services.OrganizationService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.UUID;

@Route(value = "organizations", layout = MainLayout.class)
@PageTitle("Empresas | Gutti Store")
@CssImport("./styles/organizations-view.css")
public class OrganizationsView extends VerticalLayout {

    Grid<Organization> grid = new Grid<>(Organization.class, false);
    TextField filterText = new TextField();
    OrganizationForm form;

    private final OrganizationService organizationService;

    public OrganizationsView(OrganizationService organizationService) {
        this.organizationService = organizationService;
        addClassName("organizations-view");
        setSizeFull();
        configureGrid();
        configureForm();

        Div content = new Div(grid, form);
        content.addClassName("content");
        content.setSizeFull();

        add(getToolbar(), content);
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassName("organizations-grid");
        grid.setSizeFull();
        grid.addColumn(Organization::getName).setHeader("Nombre").setSortable(true);
        grid.addColumn(Organization::getPhone).setHeader("Teléfono");
        grid.addColumn(Organization::getEmail).setHeader("Email");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event -> editOrganization(event.getValue()));
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filtrar por nombre...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addOrganizationButton = new Button("Nueva Empresa");
        addOrganizationButton.addClickListener(click -> addOrganization());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addOrganizationButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void configureForm() {
        form = new OrganizationForm();
        form.setWidth("25em");
        form.addListener(OrganizationForm.SaveEvent.class, this::saveOrganization);
        form.addListener(OrganizationForm.DeleteEvent.class, this::deleteOrganization);
        form.addListener(OrganizationForm.CloseEvent.class, e -> closeEditor());
    }

    private void saveOrganization(OrganizationForm.SaveEvent event) {
        organizationService.save(event.getOrganization());
        updateList();
        closeEditor();
    }

    private void deleteOrganization(OrganizationForm.DeleteEvent event) {
        organizationService.delete(event.getOrganization());
        updateList();
        closeEditor();
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

    private void updateList() {
        // --- LA SOLUCIÓN ESTÁ AQUÍ ---
        // Se obtiene el ID de la organización (tenant) del contexto del usuario actual
        UUID organizationId = AppRequestContext.get().getOrganizationId();
        // Se pasan ambos parámetros al servicio para filtrar correctamente
        grid.setItems(organizationService.findAll(organizationId, filterText.getValue()));
    }
}