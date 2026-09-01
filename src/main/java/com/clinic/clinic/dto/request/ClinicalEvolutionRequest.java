package com.clinic.clinic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ClinicalEvolutionRequest(
        @NotNull
        Long medicalHistoryId,
        Long appointmentId,
        @NotNull
        Long specialistId,
        @NotBlank
        String diagnosis,
        @Size(max = 5000)
        String treatment,
        @Size(max = 2000)
        String notes
) {
}
