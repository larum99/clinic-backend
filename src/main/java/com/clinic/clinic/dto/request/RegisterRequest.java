package com.clinic.clinic.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegisterRequest(
        @NotBlank
        @Size(max = 100)
        String firstName,
        @Size(max = 100)
        String lastName,
        @NotBlank
        @Email
        @Size(max = 150)
        String email,
        @Size(max = 20)
        String phone,
        @NotBlank
        @Size(min = 6, max = 255)
        String password,
        @NotBlank
        @Size(max = 30)
        String documentType,
        @NotBlank
        @Size(max = 30)
        String documentNumber,
        LocalDate birthDate
) {
}
