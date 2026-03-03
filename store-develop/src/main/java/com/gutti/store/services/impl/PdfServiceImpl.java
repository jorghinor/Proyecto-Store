package com.gutti.store.services.impl;

import com.gutti.store.dtos.CategorySalesDto;
import com.gutti.store.dtos.TopSellingProductDto;
import com.gutti.store.services.PdfService;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Service
public class PdfServiceImpl implements PdfService {

    @Override
    public ByteArrayInputStream generateCategorySalesPdf(List<CategorySalesDto> categorySales) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            font.setSize(18);
            font.setColor(java.awt.Color.BLUE);

            Paragraph p = new Paragraph("Reporte de Ventas por Categoría", font);
            p.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(p);

            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{3, 3});

            PdfPCell hcell;
            hcell = new PdfPCell(new Phrase("Categoría", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Ventas Totales", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);

            for (CategorySalesDto sale : categorySales) {
                PdfPCell cell;

                cell = new PdfPCell(new Phrase(sale.getCategoryName()));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(formatCurrency(sale.getTotalSales())));
                cell.setPaddingLeft(5);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);
            }

            document.add(table);

            document.close();

        } catch (DocumentException ex) {
            throw new RuntimeException("Error generating PDF", ex);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public ByteArrayInputStream generateTopSellingProductsPdf(List<TopSellingProductDto> topSellingProducts) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            font.setSize(18);
            font.setColor(java.awt.Color.BLUE);

            Paragraph p = new Paragraph("Reporte de Productos Más Vendidos", font);
            p.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(p);

            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{3, 3});

            PdfPCell hcell;
            hcell = new PdfPCell(new Phrase("Producto", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Unidades Vendidas", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);

            for (TopSellingProductDto product : topSellingProducts) {
                PdfPCell cell;

                cell = new PdfPCell(new Phrase(product.getProductName()));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(product.getTotalSold())));
                cell.setPaddingLeft(5);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);
            }

            document.add(table);

            document.close();

        } catch (DocumentException ex) {
            throw new RuntimeException("Error generating PDF", ex);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private String formatCurrency(Number value) {
        if (value == null) {
            value = 0;
        }
        return NumberFormat.getCurrencyInstance(Locale.US).format(value);
    }
}
