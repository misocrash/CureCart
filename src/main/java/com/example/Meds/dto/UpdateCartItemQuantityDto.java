package com.example.Meds.dto;

import lombok.Data;

@Data
public class UpdateCartItemQuantityDto {

        private Long productId;
        private int quantity;


}
