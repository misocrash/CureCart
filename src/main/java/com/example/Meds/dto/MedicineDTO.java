package com.example.Meds.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicineDTO {
    @NotBlank
    private String name;
    private String description;
    @NotNull
    @Positive
    private BigDecimal price;
    @NotNull @Positive private Integer stock;
}
