package com.dental.clinic.dto.request;

import java.time.LocalDate;

public record PatientRequest(
        Long userId,
        String documentType,
        String documentNumber,
        LocalDate birthDate,
        Boolean acceptsData,
        Boolean acceptsPromotions
) {
}