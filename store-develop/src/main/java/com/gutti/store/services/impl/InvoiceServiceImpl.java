package com.gutti.store.services.impl;

import com.gutti.store.domain.Order;
import com.gutti.store.domain.OrderItem;
import com.gutti.store.services.InvoiceService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Override
    public String generateInvoiceXml(Order order) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<Invoice>\n");
        xml.append("  <InvoiceNumber>").append(order.getInvoiceNumber()).append("</InvoiceNumber>\n");
        xml.append("  <Date>").append(order.getOrderDate().format(DateTimeFormatter.ISO_DATE_TIME)).append("</Date>\n");
        xml.append("  <Customer>\n");
        xml.append("    <Name>").append(order.getUser().getFirstName()).append(" ").append(order.getUser().getLastName()).append("</Name>\n");
        xml.append("    <Email>").append(order.getUser().getEmail()).append("</Email>\n");
        xml.append("  </Customer>\n");
        xml.append("  <Items>\n");
        for (OrderItem item : order.getItems()) {
            xml.append("    <Item>\n");
            xml.append("      <Product>").append(item.getProduct().getName()).append("</Product>\n");
            xml.append("      <Quantity>").append(item.getQuantity()).append("</Quantity>\n");
            xml.append("      <Price>").append(item.getPrice()).append("</Price>\n");
            xml.append("      <Total>").append(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))).append("</Total>\n");
            xml.append("    </Item>\n");
        }
        xml.append("  </Items>\n");
        xml.append("  <Total>").append(order.getTotal()).append("</Total>\n");
        xml.append("</Invoice>");
        return xml.toString();
    }

    @Override
    public ByteArrayInputStream generateInvoicePdf(Order order) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, java.awt.Color.BLACK);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, java.awt.Color.BLACK);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12, java.awt.Color.BLACK);

            Paragraph title = new Paragraph("Factura Electrónica", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("Número de Factura: " + order.getInvoiceNumber(), normalFont));
            document.add(new Paragraph("Fecha: " + order.getOrderDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), normalFont));
            document.add(new Paragraph("Cliente: " + order.getUser().getFirstName() + " " + order.getUser().getLastName(), normalFont));
            document.add(new Paragraph("Email: " + order.getUser().getEmail(), normalFont));

            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{4, 2, 2, 2});

            addTableHeader(table, headerFont, "Producto", "Cantidad", "Precio Unit.", "Total");

            for (OrderItem item : order.getItems()) {
                addTableCell(table, normalFont, item.getProduct().getName());
                addTableCell(table, normalFont, String.valueOf(item.getQuantity()));
                addTableCell(table, normalFont, formatCurrency(item.getPrice()));
                addTableCell(table, normalFont, formatCurrency(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))));
            }

            document.add(table);

            document.add(Chunk.NEWLINE);

            Paragraph total = new Paragraph("Total a Pagar: " + formatCurrency(order.getTotal()), titleFont);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            document.close();

        } catch (DocumentException ex) {
            throw new RuntimeException("Error generating invoice PDF", ex);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public void sendInvoice(Order order) {
        // Simulación de envío de correo electrónico
        System.out.println("Enviando factura " + order.getInvoiceNumber() + " a " + order.getUser().getEmail());
    }

    private void addTableHeader(PdfPTable table, Font font, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
            table.addCell(cell);
        }
    }

    private void addTableCell(PdfPTable table, Font font, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    private String formatCurrency(BigDecimal value) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(value);
    }
}
