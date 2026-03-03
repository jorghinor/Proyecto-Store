package com.gutti.store.services;

import com.gutti.store.dtos.CategorySalesDto;
import com.gutti.store.dtos.TopSellingProductDto;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface PdfService {
    ByteArrayInputStream generateCategorySalesPdf(List<CategorySalesDto> categorySales);
    ByteArrayInputStream generateTopSellingProductsPdf(List<TopSellingProductDto> topSellingProducts);
}
