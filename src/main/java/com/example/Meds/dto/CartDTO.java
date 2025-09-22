package com.example.Meds.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CartDTO {
    private Long cartId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
