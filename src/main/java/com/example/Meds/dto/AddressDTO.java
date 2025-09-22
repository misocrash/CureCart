package com.example.Meds.dto;

import lombok.Data;

@Data
public class AddressDTO {
    private Long addressId;
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
}
