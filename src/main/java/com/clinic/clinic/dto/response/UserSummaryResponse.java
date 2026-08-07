package com.clinic.clinic.dto.response;

public record UserSummaryResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String role
) {
}