package com.example.Meds.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class SingleOrderDetailsDTO {

    private Long orderId;
    List<MedicineDetailsDTO> medicineList;
    private String orderStatus;
    private BigDecimal totalAmount;

}
