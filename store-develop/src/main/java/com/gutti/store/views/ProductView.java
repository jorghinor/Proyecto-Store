package com.gutti.store.views;

//import com.gutti.store.configs.AppRequestContext;
import com.gutti.store.domain.Product;
import com.gutti.store.services.ProductService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
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

//import java.util.UUID;

@PermitAll
@Route(value = "products", layout = MainLayout.class)
@PageTitle("Productos | Gutti Store")
public class ProductView extends VerticalLayout {

    Grid<Product> grid = new Grid<>(Product.class);
    TextField filterText = new TextField();
    ProductForm form;
    private final ProductService productService;
    private ConfigurableFilterDataProvider<Product, Void, String> dataProvider;

    public ProductView(ProductService productService) {
        this.productService = productService;
        addClassName("product-view");
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
        form = new ProductForm();
        form.setWidth("25em");
        // --- ESTA ES LA CORRECCIÓN ---
        // Usamos los métodos públicos del formulario en lugar del 'addListener' protegido.
        form.addSaveListener(this::saveProduct);
        form.addDeleteListener(this::deleteProduct);
        form.addCloseListener(e -> closeEditor());
    }

    private void saveProduct(ProductForm.SaveEvent event) {
        // LÓGICA DE GUARDADO/ACTUALIZACIÓN CORREGIDA
        Product product = event.getProduct();
        if (product.getId() == null) {
            // Si no hay ID, es un producto nuevo
            productService.save(product);
        } else {
            // Si ya tiene ID, es una actualización
            productService.update(product.getId(), product);
        }
        dataProvider.refreshAll();
        closeEditor();
    }

    private void deleteProduct(ProductForm.DeleteEvent event) {
        productService.deleteById(event.getProduct().getId());
        dataProvider.refreshAll();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassNames("product-grid");
        grid.addClassNames("styled-grid");
        grid.setSizeFull();
        grid.setPageSize(15);
        grid.setColumns(); // Limpiamos columnas autogeneradas

        // Columna para la imagen (la solución profesional)
        grid.addComponentColumn(product -> {
            if (product.getImageUrl() != null && !product.getImageUrl().isBlank()) {
                Image image = new Image(product.getImageUrl(), "Imagen del producto");
                image.setWidth("64px");
                image.setHeight("64px");
                return image;
            }
            return new Span("Sin imagen");
        }).setHeader("Imagen").setFlexGrow(0).setWidth("100px");

        grid.addColumn(Product::getName).setHeader("Nombre");
        grid.addColumn(Product::getBrand).setHeader("Marca");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.addComponentColumn(product -> {
            Button editButton = new Button("Editar", new Icon(VaadinIcon.PENCIL));
            editButton.addClassName("grid-action-button");
            editButton.addClickListener(e -> this.editProduct(product));

            Button deleteButton = new Button("Eliminar", new Icon(VaadinIcon.TRASH));
            deleteButton.addClassName("grid-action-button");
            deleteButton.addClassName("delete");
            deleteButton.addClickListener(e -> this.confirmDelete(product));

            HorizontalLayout buttons = new HorizontalLayout(editButton, deleteButton);
            buttons.setSpacing(false);
            buttons.getThemeList().add("spacing-s");
            return buttons;
        }).setHeader("Acciones");

        // --- DATA PROVIDER PARA PAGINACIÓN Y FILTRADO ---
        DataProvider<Product, String> baseDataProvider = DataProvider.fromFilteringCallbacks(
                query -> {
                    return productService.fetchPage(query.getFilter().orElse(null), PageRequest.of(query.getPage(), query.getPageSize())).stream();
                },
                query -> {
                    return (int) productService.count(query.getFilter().orElse(null));
                }
        );

        dataProvider = baseDataProvider.withConfigurableFilter();
        grid.setDataProvider(dataProvider);
    }

    private void confirmDelete(Product product) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Eliminar '" + product.getName() + "'?");
        dialog.setText("¿Estás seguro de que quieres eliminar este producto permanentemente?");
        dialog.setConfirmText("Eliminar");
        dialog.setCancelText("Cancelar");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(event -> deleteProduct(new ProductForm.DeleteEvent(form, product)));
        dialog.open();
    }

    private Component getToolbar() {
        filterText.setPlaceholder("Filtrar por nombre...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        // Conectamos el filtro al DataProvider
        filterText.addValueChangeListener(e -> dataProvider.setFilter(e.getValue()));

        Button addProductButton = new Button("Añadir Producto");
        addProductButton.addClassName("add-button");
        addProductButton.addClickListener(click -> addProduct());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addProductButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void addProduct() {
        grid.asSingleSelect().clear();
        editProduct(new Product());
    }

    public void editProduct(Product product) {
        if (product == null) {
            closeEditor();
        } else {
            form.setProduct(product);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setProduct(null);
        form.setVisible(false);
        removeClassName("editing");
    }
}