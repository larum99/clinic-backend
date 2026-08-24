package com.clinic.clinic.dto.response;

public record PatientSummaryResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String role,
        Long patientId
) {
}
