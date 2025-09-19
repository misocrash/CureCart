package com.example.Meds.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class MedicineDetailsDTO {

    private String name;
    private BigDecimal price;
    private String manufactureName;
    private int quantity;
    private String packSize;

}
