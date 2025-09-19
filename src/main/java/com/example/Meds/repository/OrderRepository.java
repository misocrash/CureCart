package com.example.Meds.repository;

import com.example.Meds.entity.Order;
import com.example.Meds.entity.OrderStatus;
import com.example.Meds.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByUserAndStatus(User user, OrderStatus orderStatus);

    List<Order> findByUser(User user);
}
