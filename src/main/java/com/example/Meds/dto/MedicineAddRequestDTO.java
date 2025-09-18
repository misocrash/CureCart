package com.example.Meds.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data

public class MedicineAddRequestDTO {
    private String name;
    private BigDecimal price;
    private String manufacture_name;
    private String pack_size;
    private String compositionTeXT;
}
