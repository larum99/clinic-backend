package com.clinic.clinic.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MedicalHistoryRequest(
        @NotNull
        Long patientId,
        @Size(max = 5000)
        String medicalHistory,
        @Size(max = 2000)
        String allergies,
        @Size(max = 2000)
        String notes
) {
}
