package com.example.Meds.service;

import com.example.Meds.entity.Cart;
import com.example.Meds.entity.User;
import com.example.Meds.repository.CartRepository;
import com.example.Meds.repository.UsersRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CartService {

    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }


    public void createCartForUser(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());

        cartRepository.save(cart);
    }
}
