package com.gutti.store.views;

import com.gutti.store.domain.Cart;
import com.gutti.store.domain.CartItem;
import com.gutti.store.domain.Order;
import com.gutti.store.domain.User;
import com.gutti.store.services.CartService;
import com.gutti.store.services.OrderService;
import com.gutti.store.services.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@PermitAll
@Route(value = "cart", layout = MainLayout.class)
@PageTitle("Carrito de Compras | Gutti Store")
public class CartView extends VerticalLayout {

    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService;

    private Grid<CartItem> grid;
    private Span totalSpan;
    private Button checkoutButton;
    private Button clearButton;

    public CartView(CartService cartService, OrderService orderService, UserService userService) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.userService = userService;

        addClassName("cart-view");
        setSizeFull();

        configureGrid();
        configureSummary();

        add(new H2("Tu Carrito de Compras"), grid, createSummaryLayout());
        updateView();
    }

    private void configureGrid() {
        grid = new Grid<>(CartItem.class, false);
        grid.addClassName("cart-grid");
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);

        grid.addColumn(item -> item.getProduct().getName()).setHeader("Producto").setAutoWidth(true);
        grid.addColumn(item -> formatCurrency(item.getPrice())).setHeader("Precio Unitario");
        grid.addComponentColumn(item -> {
            HorizontalLayout layout = new HorizontalLayout();
            Button minus = new Button(VaadinIcon.MINUS.create(), e -> updateQuantity(item, item.getQuantity() - 1));
            Span quantity = new Span(String.valueOf(item.getQuantity()));
            Button plus = new Button(VaadinIcon.PLUS.create(), e -> updateQuantity(item, item.getQuantity() + 1));
            
            minus.addThemeVariants(ButtonVariant.LUMO_SMALL);
            plus.addThemeVariants(ButtonVariant.LUMO_SMALL);
            
            layout.add(minus, quantity, plus);
            layout.setAlignItems(Alignment.CENTER);
            return layout;
        }).setHeader("Cantidad");
        
        grid.addColumn(item -> formatCurrency(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))))
                .setHeader("Total");

        grid.addComponentColumn(item -> {
            Button removeButton = new Button(VaadinIcon.TRASH.create(), e -> removeItem(item));
            removeButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            return removeButton;
        });
    }

    private void configureSummary() {
        totalSpan = new Span();
        totalSpan.addClassName("total-text");
        totalSpan.getStyle().set("font-size", "1.5em").set("font-weight", "bold");

        checkoutButton = new Button("Pagar y Generar Factura");
        checkoutButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        checkoutButton.addClickListener(e -> checkout());

        clearButton = new Button("Vaciar Carrito");
        clearButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        clearButton.addClickListener(e -> clearCart());
    }

    private HorizontalLayout createSummaryLayout() {
        HorizontalLayout layout = new HorizontalLayout(totalSpan, clearButton, checkoutButton);
        layout.setWidthFull();
        layout.setJustifyContentMode(JustifyContentMode.END);
        layout.setAlignItems(Alignment.CENTER);
        layout.setPadding(true);
        return layout;
    }

    private void updateView() {
        User user = getCurrentUser();
        Cart cart = cartService.getCart(user);
        grid.setItems(cart.getItems());
        totalSpan.setText("Total: " + formatCurrency(cart.getTotal()));
        checkoutButton.setEnabled(!cart.getItems().isEmpty());
        clearButton.setEnabled(!cart.getItems().isEmpty());
    }

    private void updateQuantity(CartItem item, int newQuantity) {
        User user = getCurrentUser();
        cartService.updateQuantity(user, item.getProduct(), newQuantity);
        updateView();
    }

    private void removeItem(CartItem item) {
        User user = getCurrentUser();
        cartService.removeFromCart(user, item.getProduct());
        updateView();
        Notification.show("Producto eliminado del carrito");
    }

    private void clearCart() {
        User user = getCurrentUser();
        cartService.clearCart(user);
        updateView();
        Notification.show("Carrito vaciado");
    }

    private void checkout() {
        try {
            User user = getCurrentUser();
            Order order = orderService.createOrder(user);
            
            Notification.show("Compra realizada con éxito. Factura generada: " + order.getInvoiceNumber())
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            
            // Redirigir a la descarga de la factura
            getUI().ifPresent(ui -> ui.getPage().open("/orders/" + order.getId() + "/invoice", "_blank"));
            
            updateView();
            
        } catch (Exception e) {
            Notification.show("Error al procesar la compra: " + e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));
    }

    private String formatCurrency(BigDecimal value) {
        if (value == null) return "$0.00";
        return NumberFormat.getCurrencyInstance(Locale.US).format(value);
    }
}
