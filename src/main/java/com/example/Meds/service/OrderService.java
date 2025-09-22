package com.example.Meds.service;


import com.example.Meds.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public  OrderService (OrderRepository orderRepository){
        this.orderRepository=orderRepository;
    }
}
