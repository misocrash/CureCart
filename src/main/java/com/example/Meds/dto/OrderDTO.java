package com.example.Meds.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDTO {
    private Long orderId;
    private UserDTO user;
    private AddressDTO address;
    private BigDecimal totalAmount;
    private String status;
}
