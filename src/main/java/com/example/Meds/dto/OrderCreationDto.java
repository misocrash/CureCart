package com.example.Meds.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class OrderCreationDto {

    private String status;
//    private BigDecimal totalAmount;
    private Long cartId;
    private Long addressId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int userId;
}
