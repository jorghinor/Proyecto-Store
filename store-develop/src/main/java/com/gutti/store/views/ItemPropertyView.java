package com.gutti.store.views;

import com.gutti.store.configs.AppRequestContext;
import com.gutti.store.domain.ItemProperty;
import com.gutti.store.exception.ResourceInUseException;
import com.gutti.store.services.ItemPropertyService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
//import com.vaadin.flow.component.button.ButtonVariant;
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

import java.util.UUID;

@PermitAll
@Route(value = "item-properties", layout = MainLayout.class)
@PageTitle("Propiedades de Item | Gutti Store")
public class ItemPropertyView extends VerticalLayout {

    Grid<ItemProperty> grid = new Grid<>(ItemProperty.class);
    TextField filterText = new TextField();
    ItemPropertyForm form;
    private final ItemPropertyService itemPropertyService;
    private ConfigurableFilterDataProvider<ItemProperty, Void, String> dataProvider;

    public ItemPropertyView(ItemPropertyService itemPropertyService) {
        this.itemPropertyService = itemPropertyService;
        addClassName("item-property-view");
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
        form = new ItemPropertyForm();
        form.setWidth("25em");
        form.addSaveListener(this::saveProperty);
        form.addDeleteListener(this::deleteProperty);
        form.addCloseListener(e -> closeEditor());
    }

    private void saveProperty(ItemPropertyForm.SaveEvent event) {
        ItemProperty property = event.getItemProperty();
        UUID organizationId = AppRequestContext.get().getOrganizationId();

        if (property.getId() == null) {
            itemPropertyService.create(property.getLabel(), organizationId);
        } else {
            itemPropertyService.update(property.getId(), property.getLabel(), organizationId);
        }
        dataProvider.refreshAll();
        closeEditor();
    }

    private void deleteProperty(ItemPropertyForm.DeleteEvent event) {
        try {
            itemPropertyService.delete(event.getItemProperty().getId(), AppRequestContext.get().getOrganizationId());
            dataProvider.refreshAll();
            closeEditor();
        } catch (ResourceInUseException e) {
            Notification notification = Notification.show(e.getMessage(), 3000, Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void configureGrid() {
        grid.addClassNames("item-property-grid");
        grid.addClassNames("styled-grid");
        grid.setSizeFull();
        grid.setPageSize(15);
        grid.setColumns(); // Limpiamos columnas autogeneradas

        grid.addColumn(ItemProperty::getLabel).setHeader("Nombre");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.addComponentColumn(property -> {
            Button editButton = new Button("Editar", new Icon(VaadinIcon.PENCIL));
            //editButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            editButton.addClassName("grid-action-button");
            editButton.addClickListener(e -> editProperty(property));

            Button deleteButton = new Button("Eliminar", new Icon(VaadinIcon.TRASH));
            //deleteButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            deleteButton.addClassName("grid-action-button");
            deleteButton.addClassName("delete"); // Clase específica para el color rojo
            deleteButton.addClickListener(e -> confirmDelete(property));

            HorizontalLayout buttons = new HorizontalLayout(editButton, deleteButton);
            buttons.setSpacing(false);
            buttons.getThemeList().add("spacing-s");
            return buttons;
        }).setHeader("Acciones");

        DataProvider<ItemProperty, String> baseDataProvider = DataProvider.fromFilteringCallbacks(
                query -> {
                    UUID organizationId = AppRequestContext.get().getOrganizationId();
                    PageRequest pageable = PageRequest.of(query.getPage(), query.getPageSize());
                    return itemPropertyService.fetchPage(organizationId, query.getFilter().orElse(null), pageable).stream();
                },
                query -> {
                    UUID organizationId = AppRequestContext.get().getOrganizationId();
                    return (int) itemPropertyService.count(organizationId, query.getFilter().orElse(null));
                }
        );

        dataProvider = baseDataProvider.withConfigurableFilter();
        grid.setDataProvider(dataProvider);
    }

    private void confirmDelete(ItemProperty property) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Eliminar '" + property.getLabel() + "'?");
        dialog.setText("¿Estás seguro de que quieres eliminar esta propiedad permanentemente? Esta acción no se puede deshacer.");
        dialog.setConfirmText("Eliminar");
        dialog.setCancelText("Cancelar");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(event -> deleteProperty(new ItemPropertyForm.DeleteEvent(form, property)));
        dialog.open();
    }

    private Component getToolbar() {
        filterText.setPlaceholder("Filtrar por nombre...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> dataProvider.setFilter(e.getValue()));

        Button addPropertyButton = new Button("Añadir Propiedad");
        addPropertyButton.addClassName("add-button");
        addPropertyButton.addClickListener(click -> addProperty());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addPropertyButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void addProperty() {
        grid.asSingleSelect().clear();
        editProperty(new ItemProperty());
    }

    private void editProperty(ItemProperty itemProperty) {
        if (itemProperty == null) {
            closeEditor();
        } else {
            form.setItemProperty(itemProperty);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setItemProperty(null);
        form.setVisible(false);
        removeClassName("editing");
    }
}