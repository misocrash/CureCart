package com.example.Meds.controller;

import com.example.Meds.dto.CartItemDTO;
import com.example.Meds.dto.CartResponseDTO;
import com.example.Meds.mapper.CartMapper;
import com.example.Meds.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{userId}/cart") // A more RESTful URL, associating the cart with a user
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Gets the current state of the user's cart.
     */
    @GetMapping
    public ResponseEntity<CartResponseDTO> getCart(@PathVariable Integer userId) {
        CartResponseDTO cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cart);
    }

    /**
     * Adds a new item to the user's cart or increases the quantity if it already exists.
     */
    @PostMapping("/items")
    public ResponseEntity<CartResponseDTO> addItemToCart(
            @PathVariable Integer userId,
            @Valid @RequestBody CartItemDTO cartItemDTO) {
        CartResponseDTO updatedCart = cartService.addItemToCart(userId, cartItemDTO);
        return ResponseEntity.ok(updatedCart);
    }

    /**
     * Updates the quantity of an existing item in the cart.
     * If quantity is 0, the item is removed.
     */
    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponseDTO> updateItemQuantity(
            @PathVariable Long itemId,
            @RequestParam int quantity) {
        CartResponseDTO updatedCart = cartService.updateItemQuantity(itemId, quantity);
        return ResponseEntity.ok(updatedCart);
    }

    /**
     * Removes an item completely from the cart.
     */
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponseDTO> removeItemFromCart(@PathVariable Long itemId) {
        CartResponseDTO updatedCart = cartService.removeItemFromCart(itemId);
        return ResponseEntity.ok(updatedCart);
    }

    /**
     * Removes all items from the user's cart.
     */
    @DeleteMapping
    public ResponseEntity<Void> clearCart(@PathVariable Integer userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}