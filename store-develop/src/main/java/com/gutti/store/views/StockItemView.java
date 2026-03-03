package com.gutti.store.views;

import com.gutti.store.api.CreateStockItemRequest;
import com.gutti.store.api.UpdateStockItemRequest;
import com.gutti.store.configs.AppRequestContext;
import com.gutti.store.domain.Category;
import com.gutti.store.domain.Product;
import com.gutti.store.domain.ProductCategory;
import com.gutti.store.domain.StockItem;
import com.gutti.store.services.CategoryService;
import com.gutti.store.services.ProductService;
import com.gutti.store.services.StockService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

@PermitAll
@Route(value = "stock-items", layout = MainLayout.class)
@PageTitle("Items de Stock | Gutti Store")
public class StockItemView extends VerticalLayout {
    Grid<StockItem> grid = new Grid<>(StockItem.class);
    TextField filterText = new TextField();
    StockItemForm form;
    private final StockService stockService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private ConfigurableFilterDataProvider<StockItem, Void, String> dataProvider;

    @Autowired
    public StockItemView(StockService stockService, ProductService productService, CategoryService categoryService) {
        this.stockService = stockService;
        this.productService = productService;
        this.categoryService = categoryService;
        addClassName("stock-item-view");
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
        // --- LÓGICA CORREGIDA ---
        // 1. Obtenemos la lista COMPLETA de productos y categorías.
        UUID organizationId = AppRequestContext.get().getOrganizationId();
        List<Product> allProducts = productService.findAll(organizationId);
        List<Category> allCategories = categoryService.findAll(organizationId);

        // 2. Le pasamos esas listas al formulario.
        form = new StockItemForm(allProducts, allCategories);
        form.setWidth("25em");
        form.addSaveListener(this::saveStockItem);
        form.addDeleteListener(this::deleteStockItem);
        form.addCloseListener(e -> closeEditor());
    }

    private void saveStockItem(StockItemForm.SaveEvent event) {
        UUID organizationId = AppRequestContext.get().getOrganizationId();
        StockItem stockItem = event.getStockItem();

        if (stockItem.getId() == null) {
            CreateStockItemRequest request = getCreateStockItemRequest(stockItem);
            stockService.createStockItem(request, organizationId);
        } else {
            UpdateStockItemRequest request = new UpdateStockItemRequest();
            if (stockItem.getDetails() != null && !stockItem.getDetails().isEmpty()) {
                request.setQuantity(stockItem.getDetails().getFirst().getQuantity());
                request.setUnitPrice(stockItem.getDetails().getFirst().getUnitPrice());
            }
            stockService.updateStockItem(stockItem.getId(), request, organizationId);
        }
        dataProvider.refreshAll();
        closeEditor();
    }

    private static CreateStockItemRequest getCreateStockItemRequest(StockItem stockItem) {
        CreateStockItemRequest request = new CreateStockItemRequest();
        request.setProductId(stockItem.getProduct().getId());

        // La lógica para obtener el ProductCategory ID se manejará en el servicio si es necesario,
        // aquí solo nos preocupamos de la categoría seleccionada.
        if (stockItem.getProductCategory() != null) {
            request.setProductCategoryId(stockItem.getProductCategory().getId());
        }

        if (stockItem.getDetails() != null && !stockItem.getDetails().isEmpty()) {
            request.setQuantity(stockItem.getDetails().getFirst().getQuantity());
            request.setUnitPrice(stockItem.getDetails().getFirst().getUnitPrice());
        }
        return request;
    }

    private void deleteStockItem(StockItemForm.DeleteEvent event) {
        UUID organizationId = AppRequestContext.get().getOrganizationId();
        stockService.deleteById(event.getStockItem().getId(), organizationId);
        dataProvider.refreshAll();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassName("styled-grid");
        grid.setSizeFull();
        grid.setPageSize(10);
        grid.setColumns();

        grid.addColumn(item -> item.getProduct() != null ? item.getProduct().getName() : "N/A").setHeader("Producto");
        grid.addColumn(item -> (item.getProductCategory() != null && item.getProductCategory().getCategory() != null) ? item.getProductCategory().getCategory().getLabel() : "N/A").setHeader("Categoría");
        grid.addColumn(item -> (item.getDetails() != null && !item.getDetails().isEmpty()) ? item.getDetails().getFirst().getQuantity() : 0).setHeader("Cantidad");
        grid.addColumn(item -> (item.getDetails() != null && !item.getDetails().isEmpty()) ? item.getDetails().getFirst().getUnitPrice() : 0).setHeader("Precio Unitario");

        grid.addComponentColumn(item -> {
            Button editButton = new Button("Editar", new Icon(VaadinIcon.PENCIL));
            editButton.addClassName("grid-action-button");
            editButton.addClickListener(e -> this.editStockItem(item));

            Button deleteButton = new Button("Eliminar", new Icon(VaadinIcon.TRASH));
            deleteButton.addClassName("grid-action-button");
            deleteButton.addClassName("delete");
            deleteButton.addClickListener(e -> this.confirmDelete(item));

            HorizontalLayout buttons = new HorizontalLayout(editButton, deleteButton);
            buttons.setSpacing(false);
            buttons.getThemeList().add("spacing-s");
            return buttons;
        }).setHeader("Acciones");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        DataProvider<StockItem, String> baseDataProvider = DataProvider.fromFilteringCallbacks(
                query -> {
                    UUID organizationId = AppRequestContext.get().getOrganizationId();
                    String filter = query.getFilter().orElse(null);
                    PageRequest pageable = PageRequest.of(query.getPage(), query.getPageSize());
                    return stockService.fetchPage(organizationId, filter, pageable).stream();
                },
                query -> {
                    UUID organizationId = AppRequestContext.get().getOrganizationId();
                    String filter = query.getFilter().orElse(null);
                    return (int) stockService.count(organizationId, filter);
                }
        );

        dataProvider = baseDataProvider.withConfigurableFilter();
        grid.setDataProvider(dataProvider);
    }

    private void confirmDelete(StockItem item) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Eliminar '" + item.getProduct().getName() + "'");
        dialog.setText("¿Estás seguro de que quieres eliminar este item permanentemente?");
        dialog.setConfirmText("Eliminar");
        dialog.setCancelText("Cancelar");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(event -> deleteStockItem(new StockItemForm.DeleteEvent(form, item)));
        dialog.open();
    }

    private Component getToolbar() {
        filterText.setPlaceholder("Filtrar por nombre de producto...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> dataProvider.setFilter(e.getValue()));

        Button addStockItemButton = new Button("Añadir Item de Stock");
        addStockItemButton.addClassName("add-button");
        addStockItemButton.addClickListener(click -> addStockItem());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addStockItemButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void addStockItem() {
        grid.asSingleSelect().clear();
        form.setStockItem(new StockItem());
        form.setVisible(true);
        addClassName("editing");
    }

    private void closeEditor() {
        form.setStockItem(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    public void editStockItem(StockItem stockItem) {
        if (stockItem == null) {
            closeEditor();
        } else if (stockItem.getId() == null) {
            addStockItem();
        } else {
            UUID organizationId = AppRequestContext.get().getOrganizationId();
            try {
                StockItem fullStockItem = stockService.findById(stockItem.getId(), organizationId);
                form.setStockItem(fullStockItem);
                form.setVisible(true);
                addClassName("editing");
            } catch (Exception e) {
                closeEditor();
            }
        }
    }
}