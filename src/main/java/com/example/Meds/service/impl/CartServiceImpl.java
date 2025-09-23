package com.example.Meds.service.impl;

import com.example.Meds.entity.Cart;
import com.example.Meds.entity.CartItem;
import com.example.Meds.entity.Medicine;
import com.example.Meds.repository.CartItemRepository;
import com.example.Meds.repository.CartRepository;
import com.example.Meds.repository.MedicineRepository;
import com.example.Meds.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MedicineRepository medicineRepository;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           MedicineRepository medicineRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.medicineRepository = medicineRepository;
    }

    @Override
    public Cart getOrCreateCart(Integer userId) {
        return cartRepository.findByUser_Id(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId); // Assuming you have a setter or constructor
                    return cartRepository.save(newCart);
                });
    }

    @Override
    public CartItem addItemToCart(Long cartId, Long medicineId, int quantity) {
        Cart cart = cartRepository.findById(cartId).orElseThrow();
        Medicine medicine = medicineRepository.findById(medicineId).orElseThrow();

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setMedicine(medicine);
        item.setQuantity(quantity);

        return cartItemRepository.save(item);
    }

    @Override
    public CartItem updateItemQuantity(Long cartItemId, int quantity) {
        CartItem item = cartItemRepository.findById(cartItemId).orElseThrow();
        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }

    @Override
    public void removeItemFromCart(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    @Override
    public List<CartItem> getCartItems(Long cartId) {
        return cartItemRepository.findByCart_CartId(cartId);
    }

    @Override
    public void clearCart(Long cartId) {
        List<CartItem> items = cartItemRepository.findByCart_CartId(cartId);
        cartItemRepository.deleteAll(items);
    }
}