package com.dental.clinic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record DentalServiceRequest(

        @NotBlank
        @Size(max = 100)
        String name,
        String description,
        @NotNull
        @Positive
        Short durationMinutes,
        @PositiveOrZero
        BigDecimal price,
        @NotNull
        Boolean active
) {
}