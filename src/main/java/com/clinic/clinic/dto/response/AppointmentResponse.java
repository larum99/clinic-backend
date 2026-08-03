package com.clinic.clinic.dto.response;

import com.clinic.clinic.utils.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        Long patientId,
        Long specialistId,
        Long serviceId,
        AppointmentStatus status,
        LocalDateTime startDatetime,
        LocalDateTime endDatetime,
        String reason,
        String notes,
        Long createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}