package com.gutti.store.views;

import com.gutti.store.domain.Product;
import com.gutti.store.domain.User;
import com.gutti.store.services.CartService;
import com.gutti.store.services.ProductService;
import com.gutti.store.services.UserService;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.springframework.security.core.context.SecurityContextHolder;

@PermitAll
@Route(value = "catalog", layout = MainLayout.class)
@PageTitle("Catálogo de Productos | Gutti Store")
// Importamos un CSS para definir nuestra cuadrícula
@CssImport("./styles/product-catalog-view.css")
public class ProductCatalogView extends VerticalLayout {

    private final ProductService productService;
    private final CartService cartService;
    private final UserService userService;

    private Div cardContainer;
    private TextField filterText;
    private HorizontalLayout paginationControls;

    private int currentPage = 0;
    private final int pageSize = 9; // 3x3 grid

    public ProductCatalogView(ProductService productService, CartService cartService, UserService userService) {
        this.productService = productService;
        this.cartService = cartService;
        this.userService = userService;

        addClassName("product-catalog-view");
        setSizeFull();

        configureFilter();
        configureCardContainer();
        configurePagination();

        add(filterText, cardContainer, paginationControls);
        setFlexGrow(1, cardContainer); // Hacemos que el contenedor de tarjetas ocupe el espacio disponible

        updateList();
    }

    private void configureFilter() {
        filterText = new TextField();
        filterText.setPlaceholder("Buscar por producto o categoría...");
        filterText.setClearButtonVisible(true);
        filterText.setWidth("50%");
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> {
            currentPage = 0; // Reseteamos a la primera página al buscar
            updateList();
        });
    }

    private void configureCardContainer() {
        cardContainer = new Div();
        // Usamos la clase CSS que definiremos para crear la cuadrícula
        cardContainer.setWidthFull();
        // Hacemos que el contenedor de tarjetas sea scrollable si el contenido se desborda
        cardContainer.getStyle().set("overflow", "auto"); // <-- ESTA ES LA LÍNEA CLAVE
        cardContainer.addClassName("card-grid");
    }

    private void configurePagination() {
        paginationControls = new HorizontalLayout();
        paginationControls.setWidthFull();
        paginationControls.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
    }

    private void updateList() {
        // Limpiamos las tarjetas anteriores
        cardContainer.removeAll();

        // Pedimos la página de datos al servicio
        Page<Product> productPage = productService.fetchCatalogPage(
                filterText.getValue(),
                PageRequest.of(currentPage, pageSize)
        );

        // Creamos una tarjeta por cada producto y la añadimos al contenedor
        productPage.getContent().forEach(product -> {
            cardContainer.add(new ProductCard(product, this::addToCart));
        });

        // Actualizamos los controles de paginación
        updatePaginationControls(productPage.getTotalPages());
    }

    private void addToCart(Product product) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));

        cartService.addToCart(user, product, 1);
        Notification.show("Producto agregado al carrito: " + product.getName())
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void updatePaginationControls(int totalPages) {
        paginationControls.removeAll();

        if (totalPages <= 1) {
            return; // No mostramos paginación si solo hay una página
        }

        Button prevButton = new Button("Anterior", e -> {
            if (currentPage > 0) {
                currentPage--;
                updateList();
            }
        });
        prevButton.setEnabled(currentPage > 0);

        Span pageInfo = new Span("Página " + (currentPage + 1) + " de " + totalPages);

        Button nextButton = new Button("Siguiente", e -> {
            if (currentPage < totalPages - 1) {
                currentPage++;
                updateList();
            }
        });
        nextButton.setEnabled(currentPage < totalPages - 1);

        paginationControls.add(prevButton, pageInfo, nextButton);
    }
}
