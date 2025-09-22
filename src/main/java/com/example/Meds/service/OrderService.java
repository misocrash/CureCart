package com.example.Meds.service;


import com.example.Meds.entity.*;
import com.example.Meds.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {
    private final UsersRepository usersRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final AddressRepository addressRepository;
    private final CartService cartService;

    public OrderService(UsersRepository userRepository, CartRepository cartRepository, OrderRepository orderRepository, CartItemRepository cartItemRepository,AddressRepository addressRepository, CartService cartService) {
        this.usersRepository = userRepository;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.addressRepository = addressRepository;
        this.cartService = cartService;
    }

    public Long orderCreation(int userId, long addressId) {

        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address address= addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        OrderStatus orderStatus = OrderStatus.PENDING;

        Long cartId = cartService.getCurrentCartByUserId(userId).getCartId();

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        BigDecimal totalAmount = BigDecimal.valueOf(0);
        List<CartItem> cartItemList = cartItemRepository.findByCart(cart);

        for(CartItem cartItem: cartItemList){
            Medicine medicine = cartItem.getMedicine();
            BigDecimal costPerMedicine = medicine.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(costPerMedicine);
        }

        Order order = new Order(
                user,
                address,
                totalAmount,
                orderStatus,
                cart,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        Order savedOrder = orderRepository.save(order);
        return savedOrder.getOrderId();

    }
}
