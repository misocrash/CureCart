package com.example.Meds.repository;

import com.example.Meds.entity.Cart;
import com.example.Meds.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
//cartitem,=> table then the long here is for dt of the primary key in the table mentioned
public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    List<CartItem> findByCart(Cart cart);

    Optional<CartItem> findByCart_CartIdAndMedicine_Id(Long cartId, Long medicineId);

}
