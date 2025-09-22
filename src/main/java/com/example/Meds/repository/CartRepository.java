package com.example.Meds.repository;

import com.example.Meds.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findTopByUserIdOrderByCartIdDesc(int userId);

    List<Cart> findByUserId(int userId);
}
