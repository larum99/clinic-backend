package com.clinic.clinic.dto.response;

import java.time.LocalDateTime;

public record ClinicalEvolutionResponse(
        Long id,
        Long medicalHistoryId,
        Long patientId,
        Long appointmentId,
        Long specialistId,
        String diagnosis,
        String treatment,
        String notes,
        LocalDateTime registeredAt
) {
}
