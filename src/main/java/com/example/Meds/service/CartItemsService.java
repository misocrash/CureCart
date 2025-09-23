package com.example.Meds.service;


import com.example.Meds.dto.AddToCartRequestDto;
import com.example.Meds.dto.UpdateCartItemQuantityDto;
import com.example.Meds.entity.Cart;
import com.example.Meds.entity.CartItem;
import com.example.Meds.entity.Medicine;
import com.example.Meds.repository.CartItemRepository;
import com.example.Meds.repository.CartRepository;
import com.example.Meds.repository.MedicineRepository;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CartItemsService {
    private final CartItemRepository cartItemRepository;
    private  final CartRepository cartRepository;
    private final CartService cartService;
    private final MedicineRepository medicineRepository;
    public CartItemsService(CartItemRepository cartItemRepository,
                            CartRepository cartRepository,
                            CartService cartService,
                            MedicineRepository medicineRepository){
        this.cartItemRepository= cartItemRepository;
        this.cartRepository=cartRepository;
        this.medicineRepository=medicineRepository;
        this.cartService=cartService;
    }


    public void addToCart(int userId, AddToCartRequestDto requestDto) {


        Long cartId = cartService.getCurrentCartByUserId(userId).getCartId();

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        Medicine medicine = medicineRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Medicine not found"));

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setMedicine(medicine);
        cartItem.setQuantity(requestDto.getQuantity());

        cartItemRepository.save(cartItem);
    }

    public void updateQuantity(int userId, UpdateCartItemQuantityDto requestDto) {
        Cart cart = cartRepository.findTopByUserIdOrderByCartIdDesc(userId);
        if (cart == null) throw new ResourceNotFoundException("No cart found");

        Optional<CartItem> cartItemOpt = cartItemRepository.findByCart_CartIdAndMedicine_Id(
                cart.getCartId(), requestDto.getProductId()
        );
        if (cartItemOpt.isEmpty()) {
            throw new ResourceNotFoundException("Cart item not found");
        }
        CartItem cartItem = cartItemOpt.get();

        if (requestDto.getQuantity() <= 0) {
            cartItemRepository.delete(cartItem);
            cart.setUpdatedAt(LocalDateTime.now());
            cartRepository.save(cart);
        } else {
            cartItem.setQuantity(requestDto.getQuantity());
            cart.setUpdatedAt(LocalDateTime.now());
            cartItemRepository.save(cartItem);
            cartRepository.save(cart);
        }
    }
}
