package com.clinic.clinic.mapper;

import com.clinic.clinic.dto.request.SpecialistRequest;
import com.clinic.clinic.dto.response.SpecialistResponse;
import com.clinic.clinic.entity.DentalService;
import com.clinic.clinic.entity.Specialist;
import com.clinic.clinic.entity.User;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SpecialistMapper {

    public Specialist toEntity(SpecialistRequest request, User user, Set<DentalService> services) {
        Specialist specialist = new Specialist();

        specialist.setUser(user);
        specialist.setSpecialty(request.specialty());
        specialist.setProfessionalLicense(request.professionalLicense());
        specialist.setActive(request.active());
        specialist.setServices(services);

        return specialist;
    }

    public SpecialistResponse toResponse(Specialist specialist) {

        Long userId = specialist.getUser() != null
                ? specialist.getUser().getId()
                : null;

        Set<Long> serviceIds = specialist.getServices()
                .stream()
                .map(DentalService::getId)
                .collect(Collectors.toSet());

        return new SpecialistResponse(
                specialist.getId(),
                userId,
                specialist.getSpecialty(),
                specialist.getProfessionalLicense(),
                specialist.getActive(),
                serviceIds
        );
    }
}