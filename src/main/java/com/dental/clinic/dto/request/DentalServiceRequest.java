package com.dental.clinic.dto.request;

import java.math.BigDecimal;

public record DentalServiceRequest(
        String name,
        String description,
        Short durationMinutes,
        BigDecimal price,
        String status
) {
}
