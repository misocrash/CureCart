package com.example.Meds.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CartGetCurrentRequestDTO {

    private int userId;
    private Long cartId;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

}
