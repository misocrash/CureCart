package com.example.Meds.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDTO {
    private Long cartItemId;
    private Long medicineId;
    private String medicineName;
    private BigDecimal price;
    private int quantity;
}

