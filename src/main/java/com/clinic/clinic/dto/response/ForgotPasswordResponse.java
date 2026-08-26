package com.clinic.clinic.dto.response;

public record ForgotPasswordResponse(
        String message,
        String resetToken
) {
}
