package com.example.Meds.service.impl;

import com.example.Meds.dto.CartItemDTO;
import com.example.Meds.dto.CartResponseDTO;
import com.example.Meds.entity.Cart;
import com.example.Meds.entity.CartItem;
import com.example.Meds.entity.Medicine;
import com.example.Meds.entity.User;
import com.example.Meds.mapper.CartMapper;
import com.example.Meds.repository.CartItemRepository;
import com.example.Meds.repository.CartRepository;
import com.example.Meds.repository.MedicineRepository;
import com.example.Meds.repository.UsersRepository;
import com.example.Meds.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MedicineRepository medicineRepository;
    private final UsersRepository usersRepository;
    private final CartMapper cartMapper;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository,
                           MedicineRepository medicineRepository, UsersRepository usersRepository, CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.medicineRepository = medicineRepository;
        this.usersRepository = usersRepository;
        this.cartMapper = cartMapper;
    }

    @Override
    public CartResponseDTO getCartByUserId(Integer userId) {
        Cart cart = getOrCreateCartEntity(userId);
        List<CartItem> cartItems = cartItemRepository.findByCart_CartId(cart.getCartId());
        return cartMapper.toCartResponseDTO(cart, cartItems);
    }

    @Override
    @Transactional
    public CartResponseDTO addItemToCart(Integer userId, CartItemDTO cartItemDTO) {
        Cart cart = getOrCreateCartEntity(userId);
        Medicine medicine = medicineRepository.findById(cartItemDTO.getMedicineId())
                .orElseThrow(() -> new RuntimeException("Medicine not found"));

        Optional<CartItem> existingItemOpt = cartItemRepository.findByCart_CartIdAndMedicine_Id(cart.getCartId(), medicine.getId());

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + cartItemDTO.getQuantity());
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setMedicine(medicine);
            newItem.setQuantity(cartItemDTO.getQuantity());
            cartItemRepository.save(newItem);
        }

        return getCartByUserId(userId);
    }

    @Override
    @Transactional
    public CartResponseDTO updateItemQuantity(Long cartItemId, int quantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        return getCartByUserId(item.getCart().getUser().getId());
    }

    @Override
    @Transactional
    public CartResponseDTO removeItemFromCart(Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        Integer userId = item.getCart().getUser().getId();
        cartItemRepository.delete(item);
        return getCartByUserId(userId);
    }

    @Override
    @Transactional
    public void clearCart(Integer userId) {
        Cart cart = getOrCreateCartEntity(userId);
        List<CartItem> items = cartItemRepository.findByCart_CartId(cart.getCartId());
        cartItemRepository.deleteAll(items);
    }

    // Helper method to find or create a cart
    private Cart getOrCreateCartEntity(Integer userId) {
        return cartRepository.findByUser_Id(userId)
                .orElseGet(() -> {
                    User user = usersRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    Cart newCart = new Cart();
                    newCart.setUser(user);

                    return cartRepository.save(newCart);
                });
    }
}