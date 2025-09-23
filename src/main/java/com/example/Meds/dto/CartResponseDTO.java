package com.example.Meds.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDTO {
    private Long cartId;
    private Integer userId;
    private List<CartItemResponseDTO> items;
    private BigDecimal totalAmount;
}

