package com.dental.clinic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SpecialistRequest(

        @NotNull
        Long userId,

        @NotBlank
        @Size(max = 100)
        String specialty,

        @Size(max = 50)
        String professionalLicense,

        @NotNull
        Boolean active
) {
}