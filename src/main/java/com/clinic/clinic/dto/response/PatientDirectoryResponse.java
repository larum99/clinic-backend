package com.clinic.clinic.dto.response;

public record PatientDirectoryResponse(
        Long id,
        Long patientId,
        String firstName,
        String lastName,
        String email,
        String role
) {
}
