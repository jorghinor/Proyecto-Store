package com.gutti.store.services.impl;

import com.gutti.store.domain.*;
import com.gutti.store.services.CartService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository; // Assuming this exists

    @Override
    @Transactional
    public Cart getCart(User user) {
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
        Hibernate.initialize(cart.getItems());
        return cart;
    }

    @Override
    @Transactional
    public Cart addToCart(User user, Product product, int quantity) {
        Cart cart = getCart(user); // This already initializes items
        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            BigDecimal price = product.getPrice();
            if (price == null) {
                price = BigDecimal.ZERO;
            }
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .price(price)
                    .build();
            cart.addItem(newItem);
        }
        cart.recalculateTotal();
        Cart savedCart = cartRepository.save(cart);
        Hibernate.initialize(savedCart.getItems());
        return savedCart;
    }

    @Override
    @Transactional
    public Cart removeFromCart(User user, Product product) {
        Cart cart = getCart(user);
        cartItemRepository.findByCartAndProduct(cart, product).ifPresent(item -> {
            cart.removeItem(item);
            cartItemRepository.delete(item);
        });
        cart.recalculateTotal();
        Cart savedCart = cartRepository.save(cart);
        Hibernate.initialize(savedCart.getItems());
        return savedCart;
    }

    @Override
    @Transactional
    public Cart updateQuantity(User user, Product product, int quantity) {
        Cart cart = getCart(user);
        cartItemRepository.findByCartAndProduct(cart, product).ifPresent(item -> {
            if (quantity > 0) {
                item.setQuantity(quantity);
                cartItemRepository.save(item);
            } else {
                cart.removeItem(item);
                cartItemRepository.delete(item);
            }
        });
        cart.recalculateTotal();
        Cart savedCart = cartRepository.save(cart);
        Hibernate.initialize(savedCart.getItems());
        return savedCart;
    }

    @Override
    @Transactional
    public void clearCart(User user) {
        Cart cart = getCart(user);
        cart.getItems().clear();
        cart.recalculateTotal();
        cartRepository.save(cart);
    }
}
