package com.gutti.store.services.impl;

import com.gutti.store.domain.*;
import com.gutti.store.services.CartService;
import com.gutti.store.services.InvoiceService;
import com.gutti.store.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CartService cartService;
    private final InvoiceService invoiceService;
    private final OrderRepository orderRepository; // Assuming this exists

    @Override
    @Transactional
    public Order createOrder(User user) {
        Cart cart = cartService.getCart(user);
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setInvoiceNumber(UUID.randomUUID().toString()); // Simulación de número de factura
        order.setTotal(cart.getTotal());

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            order.getItems().add(orderItem);
        }

        order.setXml(invoiceService.generateInvoiceXml(order));
        orderRepository.save(order);

        invoiceService.sendInvoice(order);
        cartService.clearCart(user);

        return order;
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrder(Long orderId) {
        return orderRepository.findByIdWithItems(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }
}
