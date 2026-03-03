package com.gutti.store.views;

import com.gutti.store.configs.AppRequestContext;
import com.gutti.store.domain.ItemProperty;
import com.gutti.store.domain.ItemPropertyValue;
import com.gutti.store.exception.ResourceInUseException;
import com.gutti.store.services.ItemPropertyService;
import com.gutti.store.services.ItemPropertyValueService;
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

import java.util.List;
import java.util.UUID;

@PermitAll
@Route(value = "item-property-values", layout = MainLayout.class)
@PageTitle("Valores de Propiedad | Gutti Store")
public class ItemPropertyValueView extends VerticalLayout {

    Grid<ItemPropertyValue> grid = new Grid<>(ItemPropertyValue.class);
    TextField filterText = new TextField();
    ItemPropertyValueForm form;

    private final ItemPropertyValueService itemPropertyValueService;
    private final ItemPropertyService itemPropertyService;
    private ConfigurableFilterDataProvider<ItemPropertyValue, Void, String> dataProvider;

    public ItemPropertyValueView(ItemPropertyValueService itemPropertyValueService, ItemPropertyService itemPropertyService) {
        this.itemPropertyValueService = itemPropertyValueService;
        this.itemPropertyService = itemPropertyService;
        addClassName("item-property-value-view");
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
        // Pasamos la lista de propiedades para poblar el ComboBox en el formulario
        List<ItemProperty> allProperties = itemPropertyService.findAll();
        form = new ItemPropertyValueForm(allProperties);
        form.setWidth("25em");
        form.addSaveListener(this::saveValue);
        form.addDeleteListener(this::deleteValue);
        form.addCloseListener(e -> closeEditor());
    }

    private void saveValue(ItemPropertyValueForm.SaveEvent event) {
        ItemPropertyValue itemValue = event.getItemPropertyValue();
        UUID organizationId = AppRequestContext.get().getOrganizationId();

        if (itemValue.getId() == null) {
            // Corregido para usar el getter correcto
            itemPropertyValueService.create(itemValue.getItemProperty().getId(), itemValue.getValue(), organizationId);
        } else {
            // Corregido para usar el getter correcto
            itemPropertyValueService.update(itemValue.getId(), itemValue.getValue(), organizationId);
        }
        dataProvider.refreshAll();
        closeEditor();
    }

    private void deleteValue(ItemPropertyValueForm.DeleteEvent event) {
        try {
            itemPropertyValueService.delete(event.getItemPropertyValue().getId(), AppRequestContext.get().getOrganizationId());
            dataProvider.refreshAll();
            closeEditor();
        } catch (ResourceInUseException e) {
            Notification notification = Notification.show(e.getMessage(), 3000, Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void configureGrid() {
        grid.addClassNames("item-property-value-grid");
        grid.addClassNames("styled-grid");
        grid.setSizeFull();
        grid.setPageSize(15);
        grid.setColumns(); // Limpiamos columnas autogeneradas

        // Corregido para usar el getter correcto
        grid.addColumn(ItemPropertyValue::getValue).setHeader("Valor");
        grid.addColumn(value -> value.getItemProperty().getLabel()).setHeader("Propiedad Padre");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.addComponentColumn(value -> {
            Button editButton = new Button("Editar", new Icon(VaadinIcon.PENCIL));
            //editButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            editButton.addClassName("grid-action-button");
            editButton.addClickListener(e -> editValue(value));

            Button deleteButton = new Button("Eliminar", new Icon(VaadinIcon.TRASH));
            //deleteButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            deleteButton.addClassName("grid-action-button");
            deleteButton.addClassName("delete"); // Clase específica para el color rojo
            deleteButton.addClickListener(e -> confirmDelete(value));

            HorizontalLayout buttons = new HorizontalLayout(editButton, deleteButton);
            buttons.setSpacing(false);
            buttons.getThemeList().add("spacing-s");
            return buttons;
        }).setHeader("Acciones");

        DataProvider<ItemPropertyValue, String> baseDataProvider = DataProvider.fromFilteringCallbacks(
                query -> {
                    UUID organizationId = AppRequestContext.get().getOrganizationId();
                    PageRequest pageable = PageRequest.of(query.getPage(), query.getPageSize());
                    return itemPropertyValueService.fetchPage(organizationId, query.getFilter().orElse(null), pageable).stream();
                },
                query -> {
                    UUID organizationId = AppRequestContext.get().getOrganizationId();
                    return (int) itemPropertyValueService.count(organizationId, query.getFilter().orElse(null));
                }
        );

        dataProvider = baseDataProvider.withConfigurableFilter();
        grid.setDataProvider(dataProvider);
    }

    private void confirmDelete(ItemPropertyValue value) {
        ConfirmDialog dialog = new ConfirmDialog();
        // Corregido para usar el getter correcto
        dialog.setHeader("Eliminar Valor '" + value.getValue() + "'?");
        dialog.setText("¿Estás seguro de que quieres eliminar este valor permanentemente? Esta acción no se puede deshacer.");
        dialog.setConfirmText("Eliminar");
        dialog.setCancelText("Cancelar");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(event -> deleteValue(new ItemPropertyValueForm.DeleteEvent(form, value)));
        dialog.open();
    }

    private Component getToolbar() {
        filterText.setPlaceholder("Filtrar por valor...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> dataProvider.setFilter(e.getValue()));

        Button addValueButton = new Button("Añadir Valor de Propiedad");
        addValueButton.addClassName("add-button");
        addValueButton.addClickListener(click -> addValue());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addValueButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void addValue() {
        grid.asSingleSelect().clear();
        editValue(new ItemPropertyValue());
    }

    private void editValue(ItemPropertyValue itemValue) {
        if (itemValue == null) {
            closeEditor();
        } else {
            form.setItemPropertyValue(itemValue);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setItemPropertyValue(null);
        form.setVisible(false);
        removeClassName("editing");
    }
}