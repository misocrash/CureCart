package com.example.Meds.controller;

import com.example.Meds.dto.AddToCartRequestDto;
import com.example.Meds.service.CartItemsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cartItems")
public class CartItemsController {
    private final CartItemsService cartItemsService;

    public CartItemsController(CartItemsService cartItemsService){this.cartItemsService=cartItemsService;}

    @PostMapping("/add/{userId}")
    public ResponseEntity<?> addToCart(@PathVariable int userId,
                                       @RequestBody AddToCartRequestDto requestDto){
        cartItemsService.addToCart(userId, requestDto);
        return ResponseEntity.ok("Item added to cart");

    }

}
