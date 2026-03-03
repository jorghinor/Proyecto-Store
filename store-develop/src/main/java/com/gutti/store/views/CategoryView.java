package com.gutti.store.views;

import com.gutti.store.api.CreateCategoryPayload;
import com.gutti.store.api.UpdateCategoryPayload;
import com.gutti.store.configs.AppRequestContext;
import com.gutti.store.domain.Category;
import com.gutti.store.services.CategoryService;
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
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

@PermitAll
@Route(value = "categories", layout = MainLayout.class)
@PageTitle("Categorías | Gutti Store")
public class CategoryView extends VerticalLayout {

    Grid<Category> grid = new Grid<>(Category.class);
    TextField filterText = new TextField();
    CategoryForm form;
    private final CategoryService categoryService;
    private ConfigurableFilterDataProvider<Category, Void, String> dataProvider;

    // --- CONSTRUCTOR CORREGIDO ---
    // Ya no necesita el CategoryTypeService
    public CategoryView(CategoryService categoryService) {
        this.categoryService = categoryService;
        addClassName("category-view");
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
        // El formulario ya no necesita que le pasemos ninguna lista.
        form = new CategoryForm();
        form.setWidth("25em");
        form.addSaveListener(this::saveCategory);
        form.addDeleteListener(this::deleteCategory);
        form.addCloseListener(e -> closeEditor());
    }

    private void saveCategory(CategoryForm.SaveEvent event) {
        Category category = event.getCategory();
        if (category.getId() == null) {
            // --- LÓGICA CORREGIDA ---
            // Usamos .name() del enum, que devuelve el String "PRODUCT" o "SERVICE"
            CreateCategoryPayload payload = new CreateCategoryPayload(category.getLabel(), category.getCategoryType().name());
            categoryService.createCategory(payload);
        } else {
            UpdateCategoryPayload payload = new UpdateCategoryPayload(category.getLabel());
            categoryService.updateCategory(category.getId(), payload);
        }
        dataProvider.refreshAll();
        closeEditor();
    }

    private void deleteCategory(CategoryForm.DeleteEvent event) {
        categoryService.deleteCategory(event.getCategory().getId());
        dataProvider.refreshAll();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassName("styled-grid");
        grid.setSizeFull();
        grid.setPageSize(15);
        grid.setColumns(); // Limpiamos columnas autogeneradas

        grid.addColumn(Category::getLabel).setHeader("Nombre");
        grid.addColumn(category -> category.getCategoryType().getLabel()).setHeader("Tipo");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.addComponentColumn(category -> {
            Button editButton = new Button("Editar", new Icon(VaadinIcon.PENCIL));
            editButton.addClassName("grid-action-button");
            editButton.addClickListener(e -> editCategory(category));

            Button deleteButton = new Button("Eliminar", new Icon(VaadinIcon.TRASH));
            deleteButton.addClassName("grid-action-button");
            deleteButton.addClassName("delete");
            deleteButton.addClickListener(e -> confirmDelete(category));

            HorizontalLayout buttons = new HorizontalLayout(editButton, deleteButton);
            buttons.setSpacing(false);
            buttons.getThemeList().add("spacing-s");
            return buttons;
        }).setHeader("Acciones");

        DataProvider<Category, String> baseDataProvider = DataProvider.fromFilteringCallbacks(
                query -> {
                    UUID organizationId = AppRequestContext.get().getOrganizationId();
                    PageRequest pageable = PageRequest.of(query.getPage(), query.getPageSize());
                    return categoryService.fetchPage(organizationId, query.getFilter().orElse(null), pageable).stream();
                },
                query -> {
                    UUID organizationId = AppRequestContext.get().getOrganizationId();
                    return (int) categoryService.count(organizationId, query.getFilter().orElse(null));
                }
        );

        dataProvider = baseDataProvider.withConfigurableFilter();
        grid.setDataProvider(dataProvider);
    }

    private void confirmDelete(Category category) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Eliminar '" + category.getLabel() + "'?");
        dialog.setText("¿Estás seguro de que quieres eliminar esta categoría permanentemente?");

        dialog.setConfirmText("Eliminar");
        dialog.setCancelText("Cancelar");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(event -> deleteCategory(new CategoryForm.DeleteEvent(form, category)));
        dialog.open();
    }

    private Component getToolbar() {
        filterText.setPlaceholder("Filtrar por nombre...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> dataProvider.setFilter(e.getValue()));

        Button addCategoryButton = new Button("Añadir Categoría");
        addCategoryButton.addClassName("add-button");
        addCategoryButton.addClickListener(click -> addCategory());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addCategoryButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void addCategory() {
        grid.asSingleSelect().clear();
        editCategory(new Category());
    }

    private void editCategory(Category category) {
        if (category == null) {
            closeEditor();
        } else {
            form.setCategory(category);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setCategory(null);
        form.setVisible(false);
        removeClassName("editing");
    }
}