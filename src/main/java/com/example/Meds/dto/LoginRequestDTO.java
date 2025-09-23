package com.example.Meds.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {
    @Email
    @NotBlank
    private String email;
    @NotBlank private String password;
}
