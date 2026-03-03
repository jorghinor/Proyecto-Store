package com.gutti.store.services;

import com.gutti.store.dtos.CategorySalesDto;
import com.gutti.store.dtos.DashboardStatsDto;
import com.gutti.store.dtos.TopSellingProductDto;

import java.util.List;

public interface DashboardService {

    List<CategorySalesDto> getCategorySales();

    DashboardStatsDto getDashboardStats();

    List<TopSellingProductDto> getTopSellingProducts(int limit);

    String getCategorySalesChartHtml();

    String getTopProductsChartHtml();
}