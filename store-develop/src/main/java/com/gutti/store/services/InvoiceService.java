package com.gutti.store.services;

import com.gutti.store.domain.Order;

import java.io.ByteArrayInputStream;

public interface InvoiceService {
    String generateInvoiceXml(Order order);
    ByteArrayInputStream generateInvoicePdf(Order order);
    void sendInvoice(Order order);
}
