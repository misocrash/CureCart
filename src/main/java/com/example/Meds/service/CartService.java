package com.example.Meds.service;

    import com.example.Meds.entity.Cart;
    import com.example.Meds.entity.CartItem;

    import java.util.List;

    public interface CartService {
        Cart getOrCreateCart(Integer userId);
        CartItem addItemToCart(Long cartId, Long medicineId, int quantity);
        CartItem updateItemQuantity(Long cartItemId, int quantity);
        void removeItemFromCart(Long cartItemId);
        List<CartItem> getCartItems(Long cartId);
        void clearCart(Long cartId);
    }
