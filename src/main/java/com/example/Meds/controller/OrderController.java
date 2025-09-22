package com.example.Meds.controller;


import com.example.Meds.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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



}
