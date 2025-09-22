package com.example.Meds.service;

import com.example.Meds.dto.CartGetCurrentRequestDTO;
import com.example.Meds.entity.Cart;
import com.example.Meds.entity.User;
import com.example.Meds.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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


    public CartGetCurrentRequestDTO getCurrentCartByUserId(int userId) {

        Cart cart = cartRepository.findTopByUserIdOrderByCartIdDesc(userId);
        return new CartGetCurrentRequestDTO(
                cart.getUser().getId(),
                cart.getCartId(),
                cart.getUpdatedAt(),
                cart.getCreatedAt()
        );


    }

    public List<Cart> findCartsById(int userId) {
        return cartRepository.findByUserId(userId);
    }
}
