package com.gutti.store.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.time.Year;

@CssImport("./themes/store/layouts/main-layout.css")
public class MainLayout extends AppLayout {

    private final AccessAnnotationChecker accessChecker;

    public MainLayout(AccessAnnotationChecker accessChecker) {
        this.accessChecker = accessChecker;
        setPrimarySection(Section.DRAWER);
        createHeader();
        createDrawer();
    }

    // --- LA SOLUCIÓN ESTÁ AQUÍ ---
    // Este método es llamado por Vaadin para mostrar el contenido de la vista actual.
    // Lo vamos a sobreescribir para envolver el contenido y el footer en un layout flexible.
    @Override
    public void setContent(Component content) {
        // Creamos un contenedor principal que usará Flexbox
        Div mainContentWrapper = new Div();
        mainContentWrapper.addClassName("main-content-wrapper");

        // Añadimos el contenido de la vista (ej: DashboardView, StockItemView)
        mainContentWrapper.add(content);
        // Añadimos el footer
        mainContentWrapper.add(createFooter());

        // Establecemos este nuevo contenedor como el contenido principal
        super.setContent(mainContentWrapper);
    }

    private void createHeader() {
        H1 logo = new H1("Gutti Store");
        logo.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.Margin.MEDIUM);

        // Se cambia la acción del botón para usar el método setLocation de Vaadin,
        // que es más limpio y seguro que ejecutar JavaScript directamente.
        Button logoutButton = new Button("Cerrar Sesión", e -> {
            UI.getCurrent().getPage().setLocation("/logout");
        });

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, logoutButton);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidth("100%");
        header.addClassNames(
                LumoUtility.Padding.Vertical.NONE,
                LumoUtility.Padding.Horizontal.MEDIUM);

        header.addClassName("app-header");
        addToNavbar(header);
    }

    private void createDrawer() {
        VerticalLayout menu = new VerticalLayout();

        // Enlaces visibles para todos los usuarios autenticados
        menu.add(createMenuLink("Dashboard", DashboardView.class));
        menu.add(createMenuLink("Mi Empresa", OrganizationDetailsView.class));
        menu.add(createMenuLink("Categorías", CategoryView.class));
        menu.add(createMenuLink("Productos", ProductView.class));
        menu.add(createMenuLink("Stock", StockItemView.class));
        menu.add(createMenuLink("Propiedades", ItemPropertyView.class));
        menu.add(createMenuLink("Valores Propiedad", ItemPropertyValueView.class));
        menu.add(createMenuLink("Catalogo", ProductCatalogView.class));
        menu.add(createMenuLink("Carrito", CartView.class)); // Added Cart link

        // Enlaces solo visibles para administradores
        if (accessChecker.hasAccess(OrganizationsAdminView.class)) {
            menu.add(createMenuLink("Admin: Empresas", OrganizationsAdminView.class));
        }
        menu.add(createMenuLink("Roles", RoleView.class));
        menu.add(createMenuLink("Usuarios", UserView.class));

        menu.setPadding(false);
        menu.setSpacing(false);
        menu.addClassName("menu-links-container");

        addToDrawer(menu);
    }

    private Footer createFooter() {
        Footer footer = new Footer();
        footer.addClassName("app-footer");
        String currentYear = Year.now().toString();
        footer.add(new Span("Gutti Store © " + currentYear));
        return footer;
    }

    private RouterLink createMenuLink(String text, Class<? extends Component> navigationTarget) {
        RouterLink link = new RouterLink(text, navigationTarget);
        link.addClassName("menu-link");
        return link;
    }
}
