package com.example.Meds.controller;

import com.example.Meds.entity.Cart;
import com.example.Meds.entity.CartItem;
import com.example.Meds.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Cart> getOrCreateCart(@PathVariable Integer userId) {
        return ResponseEntity.ok(cartService.getOrCreateCart(userId));
    }

    @PostMapping("/items")
    public ResponseEntity<CartItem> addItem(@RequestParam Long cartId,
                                            @RequestParam Long medicineId,
                                            @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.addItemToCart(cartId, medicineId, quantity));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartItem> updateQuantity(@PathVariable Long itemId,
                                                   @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.updateItemQuantity(itemId, quantity));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long itemId) {
        cartService.removeItemFromCart(itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{cartId}/items")
    public ResponseEntity<List<CartItem>> getItems(@PathVariable Long cartId) {
        return ResponseEntity.ok(cartService.getCartItems(cartId));
    }

    @DeleteMapping("/{cartId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable Long cartId) {
        cartService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }
}
