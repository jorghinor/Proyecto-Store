package com.gutti.store.services;

import com.gutti.store.domain.Order;
import com.gutti.store.domain.User;

import java.util.List;

public interface OrderService {
    Order createOrder(User user);
    Order getOrder(Long orderId);
    List<Order> getOrdersByUser(User user);
}
