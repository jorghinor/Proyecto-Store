package com.gutti.store.services;

import com.gutti.store.domain.Cart;
import com.gutti.store.domain.Product;
import com.gutti.store.domain.User;

public interface CartService {
    Cart getCart(User user);
    Cart addToCart(User user, Product product, int quantity);
    Cart removeFromCart(User user, Product product);
    Cart updateQuantity(User user, Product product, int quantity);
    void clearCart(User user);
}
