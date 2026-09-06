package com.clinic.clinic.mapper;

import com.clinic.clinic.dto.request.MedicalHistoryRequest;
import com.clinic.clinic.dto.response.MedicalHistoryResponse;
import com.clinic.clinic.entity.MedicalHistory;
import com.clinic.clinic.entity.Patient;
import org.springframework.stereotype.Component;

@Component
public class MedicalHistoryMapper {

    public MedicalHistory toEntity(MedicalHistoryRequest request, Patient patient) {
        MedicalHistory medicalHistory = new MedicalHistory();
        medicalHistory.setPatient(patient);
        medicalHistory.setMedicalHistory(request.medicalHistory());
        medicalHistory.setAllergies(request.allergies());
        medicalHistory.setNotes(request.notes());
        return medicalHistory;
    }

    public void updateEntity(MedicalHistory medicalHistory, MedicalHistoryRequest request) {
        medicalHistory.setMedicalHistory(request.medicalHistory());
        medicalHistory.setAllergies(request.allergies());
        medicalHistory.setNotes(request.notes());
    }

    public MedicalHistoryResponse toResponse(MedicalHistory medicalHistory) {
        return new MedicalHistoryResponse(
                medicalHistory.getId(),
                medicalHistory.getPatient().getId(),
                medicalHistory.getMedicalHistory(),
                medicalHistory.getAllergies(),
                medicalHistory.getNotes(),
                medicalHistory.getCreatedAt(),
                medicalHistory.getUpdatedAt()
        );
    }
}
