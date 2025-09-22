package com.example.Meds.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class AddToCartRequestDto {
    private int productId;
    private int quantity;
}
