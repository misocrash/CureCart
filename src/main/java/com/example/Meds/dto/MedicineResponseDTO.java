package com.example.Meds.dto;


import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicineResponseDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
}


