package com.example.Meds.dto;

import com.example.Meds.entity.OrderStatus;
import lombok.Data;

@Data
public class OrderStatusUpdateRequestDTO {
    private OrderStatus status;
}
