package com.example.Meds.controller;


import com.example.Meds.dto.AddressDTO;
import com.example.Meds.dto.OrderDTO;
import com.example.Meds.dto.OrderStatusUpdateRequestDTO;
import com.example.Meds.dto.UserDTO;
import com.example.Meds.entity.Address;
import com.example.Meds.entity.Order;
import com.example.Meds.entity.User;
import com.example.Meds.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService){this.orderService=orderService;}

    @PostMapping("/create/{userId}/{addressId}")
    public ResponseEntity<?> orderCreation(@PathVariable int userId,
                                           @PathVariable long addressId){
        Long orderId = orderService.orderCreation(userId,addressId);
        return new ResponseEntity<>("Order Placed!! Order Id: " + orderId, HttpStatus.CREATED);
    }

    // SEND STATUS IN THE BODY
    // {"status":"SHIPPED"}
    @PutMapping("/admin/{orderId}")
    public ResponseEntity<?> updateOrderStatus(@PathVariable long orderId, @RequestBody OrderStatusUpdateRequestDTO request) {
        orderService.updateOrderStatus(orderId, request.getStatus());
        return ResponseEntity.ok("Status Updated");
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable long orderId) {
        Optional<Order> order = orderService.getOrderById(orderId);
        if (order.isEmpty()) {
            return ResponseEntity.ok("Not found");
        }
        OrderDTO dto = new OrderDTO();

        dto.setOrderId(order.get().getOrderId());
        dto.setUser(convertUser(order.get().getUser()));
        dto.setAddress(convertAddress(order.get().getAddress()));
        dto.setTotalAmount(order.get().getTotalAmount());
        dto.setStatus(order.get().getStatus().name());

        return ResponseEntity.ok(dto);
    }

        public UserDTO convertUser(User user) {
            UserDTO dto = new UserDTO();
            dto.setId(user.getId());
            dto.setName(user.getName());
            dto.setEmail(user.getEmail());
            return dto;
        }

        public AddressDTO convertAddress(Address address) {
            AddressDTO dto = new AddressDTO();
            dto.setAddressId(address.getAddressId());
            dto.setLine1(address.getLine1());
            dto.setLine2(address.getLine2());
            dto.setCity(address.getCity());
            dto.setState(address.getState());
            dto.setCountry(address.getCountry());
            dto.setPostalCode(address.getPostalCode());
            return dto;
        }

//    public CartDTO convertCart(Cart cart) {
//        CartDTO cartDTO = new CartDTO();
//        cartDTO.setCartId(cart.getCartId());
//        cartDTO.setCreatedAt(cart.getCreatedAt());
//        cartDTO.setUpdatedAt(cart.getUpdatedAt());
//        return cartDTO;
//    }

}
