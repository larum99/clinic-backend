package com.clinic.clinic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AppointmentRequest(
        Long patientId,
        @NotNull
        Long specialistId,
        Long serviceId,
        @NotNull
        LocalDateTime startDatetime,
        @NotBlank
        @Size(max = 150)
        String reason,
        @Size(max = 500)
        String notes,
        String status
) {
}
