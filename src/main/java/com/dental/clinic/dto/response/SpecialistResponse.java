package com.dental.clinic.dto.response;

public record SpecialistResponse(
        Long id,
        Long userId,
        String specialty,
        String professionalLicense,
        Boolean active

) {
}