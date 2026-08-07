package com.clinic.clinic.dto.response;

public record LoginResponse(
        String accessToken,
        String tokenType,
        Long expiresIn,
        UserSummaryResponse user
) {
}