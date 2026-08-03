package com.clinic.clinic.mapper;

import com.clinic.clinic.dto.request.DentalServiceRequest;
import com.clinic.clinic.dto.response.DentalServiceResponse;
import com.clinic.clinic.entity.DentalService;
import org.springframework.stereotype.Component;

@Component
public class DentalServiceMapper {
    public DentalService toEntity(DentalServiceRequest request) {
        DentalService service = new DentalService();

        service.setName(request.name());
        service.setDescription(request.description());
        service.setDurationMinutes(request.durationMinutes());
        service.setPrice(request.price());
        service.setActive(request.active());

        return service;
    }

    public DentalServiceResponse toResponse(DentalService service) {
        return new DentalServiceResponse(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getDurationMinutes(),
                service.getPrice(),
                service.getActive()
        );
    }
}
