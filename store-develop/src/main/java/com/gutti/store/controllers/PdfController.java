package com.gutti.store.controllers;

import com.gutti.store.dtos.CategorySalesDto;
import com.gutti.store.dtos.TopSellingProductDto;
import com.gutti.store.services.DashboardService;
import com.gutti.store.services.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/pdf")
@RequiredArgsConstructor
public class PdfController {

    private final DashboardService dashboardService;
    private final PdfService pdfService;

    @GetMapping(value = "/category-sales", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> categorySalesPdf() {
        List<CategorySalesDto> categorySales = dashboardService.getCategorySales();
        ByteArrayInputStream bis = pdfService.generateCategorySalesPdf(categorySales);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=category-sales.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    @GetMapping(value = "/top-products", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> topProductsPdf() {
        List<TopSellingProductDto> topProducts = dashboardService.getTopSellingProducts(5);
        ByteArrayInputStream bis = pdfService.generateTopSellingProductsPdf(topProducts);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=top-products.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}
