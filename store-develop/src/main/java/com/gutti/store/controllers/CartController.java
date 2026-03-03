package com.gutti.store.controllers;

import com.gutti.store.domain.Cart;
import com.gutti.store.domain.Product;
import com.gutti.store.domain.User;
import com.gutti.store.services.CartService;
import com.gutti.store.services.ProductService;
import com.gutti.store.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final ProductService productService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Cart> getCart(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(cartService.getCart(user));
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(@AuthenticationPrincipal User user, @RequestParam Long productId, @RequestParam int quantity) {
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(cartService.addToCart(user, product, quantity));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Cart> removeFromCart(@AuthenticationPrincipal User user, @RequestParam Long productId) {
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(cartService.removeFromCart(user, product));
    }

    @PutMapping("/update")
    public ResponseEntity<Cart> updateQuantity(@AuthenticationPrincipal User user, @RequestParam Long productId, @RequestParam int quantity) {
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(cartService.updateQuantity(user, product, quantity));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal User user) {
        cartService.clearCart(user);
        return ResponseEntity.ok().build();
    }
}
