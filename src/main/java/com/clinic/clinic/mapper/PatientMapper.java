package com.clinic.clinic.mapper;

import com.clinic.clinic.dto.request.PatientRequest;
import com.clinic.clinic.dto.response.PatientDirectoryResponse;
import com.clinic.clinic.dto.response.PatientResponse;
import com.clinic.clinic.entity.Patient;
import com.clinic.clinic.entity.User;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public Patient toEntity(PatientRequest request, User user) {
        Patient patient = new Patient();

        patient.setUser(user);
        patient.setDocumentType(request.documentType());
        patient.setDocumentNumber(request.documentNumber());
        patient.setBirthDate(request.birthDate());
        patient.setAcceptsData(request.acceptsData());
        patient.setAcceptsPromotions(request.acceptsPromotions());

        return patient;
    }

    public PatientResponse toResponse(Patient patient) {
        Long userId = patient.getUser() != null
                ? patient.getUser().getId()
                : null;

        return new PatientResponse(
                patient.getId(),
                userId,
                patient.getDocumentType(),
                patient.getDocumentNumber(),
                patient.getBirthDate(),
                patient.getAcceptsData(),
                patient.getAcceptsPromotions()
        );
    }

    public PatientDirectoryResponse toDirectoryResponse(Patient patient) {
        User user = patient.getUser();

        Long userId = user != null
                ? user.getId()
                : null;

        String firstName = user != null
                ? user.getFirstName()
                : null;

        String lastName = user != null
                ? user.getLastName()
                : null;

        String email = user != null
                ? user.getEmail()
                : null;

        return new PatientDirectoryResponse(
                userId,
                patient.getId(),
                firstName,
                lastName,
                email,
                "PACIENTE"
        );
    }
}