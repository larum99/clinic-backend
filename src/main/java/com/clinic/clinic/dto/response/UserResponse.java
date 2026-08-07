package com.clinic.clinic.dto.response;

import com.clinic.clinic.enums.UserStatus;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        Long roleId,
        String roleName,
        String firstName,
        String lastName,
        String email,
        String phone,
        UserStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime lastLogin

) {
}