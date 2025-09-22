package com.example.Meds.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderDetailsRequestDTO {
    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
}
