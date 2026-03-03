package com.gutti.store.services.impl;

import com.gutti.store.configs.AppRequestContext;
import com.gutti.store.domain.StockItemDetailRepository;
import com.gutti.store.domain.StockItemRepository;
import com.gutti.store.dtos.CategorySalesDto;
import com.gutti.store.dtos.DashboardStatsDto;
import com.gutti.store.dtos.TopSellingProductDto;
import com.gutti.store.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final StockItemRepository stockItemRepository;
    private final StockItemDetailRepository stockItemDetailRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategorySalesDto> getCategorySales() {
        UUID organizationId = AppRequestContext.get().getOrganizationId();
        return stockItemDetailRepository.findTotalSalesPerCategory(organizationId);
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats() {
        UUID organizationId = AppRequestContext.get().getOrganizationId();

        BigDecimal totalSales = Optional.ofNullable(stockItemDetailRepository.sumTotalSalesByOrganizationId(organizationId))
                .orElse(BigDecimal.ZERO);
        long totalTransactions = stockItemRepository.countByOrganizationId(organizationId);
        long totalProductsInStock = Optional.ofNullable(stockItemDetailRepository.sumTotalQuantityByOrganizationId(organizationId))
                .orElse(0L);

        return new DashboardStatsDto(totalSales, totalTransactions, totalProductsInStock);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopSellingProductDto> getTopSellingProducts(int limit) {
        UUID organizationId = AppRequestContext.get().getOrganizationId();
        return stockItemDetailRepository.findTopSellingProducts(organizationId).stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public String getCategorySalesChartHtml() {
        List<CategorySalesDto> salesData = getCategorySales();

        if (salesData.isEmpty()) {
            return """
                    <!DOCTYPE html><html><head><style>body { font-family: sans-serif; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; color: #888; }</style></head><body><div>No hay datos de ventas para mostrar.</div></body></html>
                    """;
        }

        StringJoiner labels = new StringJoiner("', '", "'", "'");
        StringJoiner data = new StringJoiner(", ");
        salesData.forEach(sale -> {
            labels.add(sale.getCategoryName());
            data.add(sale.getTotalSales().toString());
        });

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Chart</title>
                    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
                    <style>
                        body { font-family: sans-serif; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }
                        #myChart { max-width: 100%%; max-height: 100%%; }
                    </style>
                </head>
                <body>
                    <canvas id="myChart"></canvas>
                    <script>
                        const ctx = document.getElementById('myChart');
                        new Chart(ctx, {
                            type: 'pie',
                            data: {
                                labels: [%s],
                                datasets: [{
                                    label: 'Ventas',
                                    data: [%s],
                                    backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF', '#FF9F40', '#C9CBCF'],
                                    hoverOffset: 4
                                }]
                            },
                            options: {
                                responsive: true,
                                plugins: {
                                    legend: {
                                        position: 'top',
                                    }
                                }
                            }
                        });
                    </script>
                </body>
                </html>
                """.formatted(labels.toString(), data.toString());
    }

    @Override
    @Transactional(readOnly = true)
    public String getTopProductsChartHtml() {
        List<TopSellingProductDto> topProducts = getTopSellingProducts(5);

        if (topProducts.isEmpty()) {
            return """
                    <!DOCTYPE html><html><head><style>body { font-family: sans-serif; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; color: #888; }</style></head><body><div>No hay datos de productos para mostrar.</div></body></html>
                    """;
        }

        StringJoiner labels = new StringJoiner("', '", "'", "'");
        StringJoiner data = new StringJoiner(", ");
        topProducts.forEach(product -> {
            labels.add(product.getProductName());
            data.add(product.getTotalSold().toString());
        });

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Chart</title>
                    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
                    <style>
                        body { font-family: sans-serif; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }
                        #myChart { max-width: 100%%; max-height: 100%%; }
                    </style>
                </head>
                <body>
                    <canvas id="myChart"></canvas>
                    <script>
                        const ctx = document.getElementById('myChart');
                        new Chart(ctx, {
                            type: 'bar',
                            data: {
                                labels: [%s],
                                datasets: [{
                                    label: 'Unidades Vendidas',
                                    data: [%s],
                                    backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF']
                                }]
                            },
                            options: {
                                indexAxis: 'y', // Para hacer las barras horizontales y que los nombres largos se lean mejor
                                responsive: true,
                                plugins: {
                                    legend: {
                                        display: false // No necesitamos leyenda para un solo dataset
                                    }
                                }
                            }
                        });
                    </script>
                </body>
                </html>
                """.formatted(labels.toString(), data.toString());
    }
}