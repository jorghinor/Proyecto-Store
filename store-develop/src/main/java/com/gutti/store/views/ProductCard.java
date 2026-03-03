package com.gutti.store.views;

import com.gutti.store.domain.Product;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Consumer;

// Importamos el CSS para los nuevos "badges"
@CssImport("./styles/product-card-badges.css")
public class ProductCard extends VerticalLayout {

    private final Consumer<Product> onAddToCart;

    public ProductCard(Product product, Consumer<Product> onAddToCart) {
        this.onAddToCart = onAddToCart;
        init(product);
    }

    public ProductCard(Product product) {
        this(product, null);
    }

    private void init(Product product) {
        // --- 1. Estilos de la Tarjeta Principal (Borde, Sombra, etc.) ---
        getStyle().set("border", "2px solid #800000"); // Borde guindo
        getStyle().set("box-shadow", "0 0 12px 4px #39ff14"); // Sombra verde fosforescente
        getStyle().set("border-radius", "var(--lumo-border-radius-l)");

        // Quitamos los espacios por defecto para tener control total
        setPadding(false);
        setSpacing(false);
        // Hacemos que la tarjeta ocupe el espacio de la celda y tenga una altura mínima
        setSizeFull();
        setMinHeight("450px"); // Altura mínima para asegurar que todo quepa

        // --- 2. Contenedor de la Imagen ---
        // Usamos un Div para que pueda contener tanto la imagen como los badges
        Div imageContainer = new Div();
        imageContainer.setHeight("200px");
        imageContainer.setWidth("100%");
        imageContainer.getStyle().set("position", "relative"); // Punto de anclaje para los badges
        // Le decimos al layout que este contenedor no debe encogerse
        imageContainer.getStyle().set("flex-shrink", "0");
        // Hacemos que la imagen sea clicable
        imageContainer.getStyle().set("cursor", "pointer");
        imageContainer.addClickListener(event -> openProductDetailDialog(product));

        Image image = new Image(
                (product.getImageUrl() != null && !product.getImageUrl().isBlank())
                        ? product.getImageUrl()
                        : "images/placeholder.png", // Una imagen por defecto si no hay URL
                "Imagen del producto"
        );
        image.setWidth("100%");
        image.setHeight("100%");
        image.getStyle().set("object-fit", "cover"); // Evita que la imagen se deforme

        imageContainer.add(image);

        // --- 3. Lógica MEJORADA para añadir los "badges" de oferta ---
        // Creamos un contenedor para los badges
        HorizontalLayout badgeContainer = new HorizontalLayout();
        badgeContainer.addClassName("badge-container");

        if (product.getDiscountPercentage() != null && product.getDiscountPercentage().doubleValue() > 0) {
            Span discountBadge = new Span(product.getDiscountPercentage().intValue() + "% OFF");
            discountBadge.addClassNames("badge", "badge-discount");
            badgeContainer.add(discountBadge);
        }

        if (product.isOnPromotion()) {
            Span promoBadge = new Span("PROMO");
            promoBadge.addClassNames("badge", "badge-promotion");
            badgeContainer.add(promoBadge);
        }

        if (product.isNewArrival()) {
            Span newBadge = new Span("NUEVO");
            newBadge.addClassNames("badge", "badge-new");
            badgeContainer.add(newBadge);
        }

        // Añadimos el contenedor de badges a la tarjeta si tiene contenido
        if (badgeContainer.getComponentCount() > 0) {
            imageContainer.add(badgeContainer);
        }

        // --- 4. Contenedor para la Información ---
        VerticalLayout infoLayout = new VerticalLayout();
        infoLayout.setPadding(true);
        infoLayout.setSpacing(false);
        infoLayout.getThemeList().add("spacing-s");
        infoLayout.setAlignItems(Alignment.CENTER);
        infoLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        // Hacemos que el layout de información ocupe todo el espacio vertical disponible
        infoLayout.setHeightFull();

        // Aplicamos el degradado
        infoLayout.getStyle().set("background", "linear-gradient(to bottom, rgba(57, 255, 20, 0.4), transparent)");

        // Nombre del Producto
        Span name = new Span(product.getName());
        // Aplicamos el estilo naranja brillante con sombra amarilla
        name.getStyle().set("color", "#FF7043");
        name.getStyle().set("text-shadow", "1px 1px 2px #FFF9C4");
        name.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.FontWeight.BOLD);

        // Marca del Producto
        Span brand = new Span(product.getBrand());
        // Aplicamos también el estilo a la marca
        brand.getStyle().set("color", "#FF7043");
        brand.getStyle().set("text-shadow", "1px 1px 2px #FFF9C4");
        brand.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.FontWeight.BOLD);

        // Precio del Producto
        Span price = new Span(formatCurrency(product.getPrice()));
        price.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.FontWeight.BOLD);
        price.getStyle().set("color", "#000000"); // Negro para el precio

        // Información de la Empresa
        Span companyInfo = new Span();
        if (product.getOrganization() != null) {
            String companyName = product.getOrganization().getName() != null ? product.getOrganization().getName() : "";
            String companyPhone = product.getOrganization().getPhone() != null ? " | " + product.getOrganization().getPhone() : "";
            companyInfo.setText(companyName + companyPhone);
        }
        companyInfo.addClassNames(LumoUtility.FontSize.XSMALL, LumoUtility.TextColor.TERTIARY, LumoUtility.Margin.Top.SMALL);

        infoLayout.add(name, brand, price, companyInfo);

        // --- 5. Botón Agregar al Carrito ---
        if (onAddToCart != null) {
            Button addToCartButton = new Button("Agregar", VaadinIcon.CART.create());
            addToCartButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
            addToCartButton.addClickListener(e -> onAddToCart.accept(product));
            addToCartButton.setWidthFull();
            infoLayout.add(addToCartButton);
        }

        // --- 6. Añadimos la imagen y la información a la tarjeta principal ---
        add(imageContainer, infoLayout);
    }

    private String formatCurrency(BigDecimal value) {
        if (value == null) return "$0.00";
        return NumberFormat.getCurrencyInstance(Locale.US).format(value);
    }

    /**
     * Abre un Dialog modal para mostrar una vista ampliada de la tarjeta del producto.
     * @param product El producto a mostrar.
     */
    private void openProductDetailDialog(Product product) {
        Dialog dialog = new Dialog();

        // Creamos una nueva instancia de la tarjeta para el diálogo, pero más grande.
        // Pasamos el mismo listener para que el botón funcione también en el diálogo
        ProductCard largeCard = new ProductCard(product, onAddToCart);
        largeCard.setWidth("400px");
        largeCard.setMinHeight("600px");

        // Añadimos la tarjeta grande al diálogo
        dialog.add(largeCard);

        // Quitamos el padding y los componentes por defecto del diálogo
        dialog.getHeader().removeAll();
        dialog.getFooter().removeAll();
        dialog.addThemeVariants(DialogVariant.LUMO_NO_PADDING);

        dialog.setResizable(false); // Evitamos que el usuario cambie el tamaño

        dialog.open();
    }
}
