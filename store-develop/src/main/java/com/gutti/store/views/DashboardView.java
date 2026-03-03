package com.gutti.store.views;

import com.gutti.store.dtos.DashboardStatsDto;
import com.gutti.store.services.DashboardService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;

import java.text.NumberFormat;
import java.util.Locale;

@PermitAll
@Route(value = "", layout = MainLayout.class)
@PageTitle("Dashboard | Store Admin")
@CssImport("./themes/store/views/dashboard-view.css")
public class DashboardView extends VerticalLayout {

    private final DashboardService dashboardService;

    public DashboardView(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
        addClassName("dashboard-view");
        setSizeFull();

        // Obtenemos todas las estadísticas del servicio
        DashboardStatsDto stats = dashboardService.getDashboardStats();

        // Layout para los dos gráficos
        HorizontalLayout chartsLayout = new HorizontalLayout(
                createChartCard("Ventas por Categoría", createCategorySalesChart(), "/pdf/category-sales"),
                createChartCard("Top 5 Productos Vendidos", createTopProductsChart(), "/pdf/top-products")
        );
        chartsLayout.setWidthFull();
        chartsLayout.addClassName(LumoUtility.Gap.LARGE);

        // Añadimos los componentes a la vista con espaciado
        add(
                createStatsLayout(stats),
                chartsLayout
        );
        getStyle().set("padding", "var(--lumo-space-l)");
    }

    /**
     * Crea el layout horizontal con las tarjetas de KPIs.
     */
    private Component createStatsLayout(DashboardStatsDto stats) {
        HorizontalLayout statsLayout = new HorizontalLayout();
        statsLayout.setWidthFull();
        statsLayout.setJustifyContentMode(JustifyContentMode.AROUND);
        statsLayout.addClassName(LumoUtility.Gap.LARGE);

        statsLayout.add(
                createStatCard("Ventas Totales", formatCurrency(stats.getTotalSales()), 1),
                createStatCard("Transacciones Totales", String.valueOf(stats.getTotalTransactions()), 2),
                createStatCard("Productos en Stock", String.valueOf(stats.getTotalProductsInStock()), 3)
        );

        return statsLayout;
    }

    /**
     * Crea una tarjeta individual para un KPI.
     */
    private Component createStatCard(String title, String value, int styleIndex) {
        VerticalLayout card = new VerticalLayout();
        card.addClassName("stat-card");
        card.addClassName("stat-card-" + styleIndex);
        card.setAlignItems(Alignment.CENTER);
        card.setJustifyContentMode(JustifyContentMode.CENTER);

        Span titleSpan = new Span(title);
        titleSpan.addClassName("stat-title");

        Span valueSpan = new Span(value);
        valueSpan.addClassName("stat-value");

        card.add(titleSpan, valueSpan);
        return card;
    }

    /**
     * Crea una tarjeta contenedora para un gráfico.
     */
    private Component createChartCard(String title, Component chartComponent, String pdfUrl) {
        VerticalLayout chartCard = new VerticalLayout();
        chartCard.addClassName("chart-card");
        chartCard.setAlignItems(Alignment.CENTER);

        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);

        H2 titleComponent = new H2(title);
        titleComponent.getStyle().set("margin", "0");

        Button pdfButton = new Button("Exportar PDF", VaadinIcon.FILE_TEXT.create());
        Anchor pdfAnchor = new Anchor(pdfUrl, pdfButton);
        pdfAnchor.setTarget("_blank");
        // Remove default anchor styles to make it look like just the button
        pdfAnchor.getElement().getStyle().set("text-decoration", "none");

        header.add(titleComponent, pdfAnchor);

        chartCard.add(header);
        chartCard.add(chartComponent);
        chartCard.expand(chartComponent); // Permite que el gráfico ocupe el espacio
        return chartCard;
    }

    /**
     * Crea el IFrame para el gráfico de ventas por categoría.
     */
    private Component createCategorySalesChart() {
        String chartHtml = dashboardService.getCategorySalesChartHtml();
        IFrame chartFrame = new IFrame();
        chartFrame.setSrcdoc(chartHtml);
        chartFrame.setSizeFull();
        chartFrame.getElement().getStyle().set("border", "none");
        return chartFrame;
    }

    /**
     * Crea el IFrame para el gráfico de productos más vendidos.
     */
    private Component createTopProductsChart() {
        String chartHtml = dashboardService.getTopProductsChartHtml();
        IFrame chartFrame = new IFrame();
        chartFrame.setSrcdoc(chartHtml);
        chartFrame.setSizeFull();
        chartFrame.getElement().getStyle().set("border", "none");
        return chartFrame;
    }

    private String formatCurrency(Number value) {
        if (value == null) {
            value = 0;
        }
        return NumberFormat.getCurrencyInstance(Locale.US).format(value);
    }
}
