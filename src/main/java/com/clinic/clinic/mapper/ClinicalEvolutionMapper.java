package com.clinic.clinic.mapper;

import com.clinic.clinic.dto.request.ClinicalEvolutionRequest;
import com.clinic.clinic.dto.response.ClinicalEvolutionResponse;
import com.clinic.clinic.entity.Appointment;
import com.clinic.clinic.entity.ClinicalEvolution;
import com.clinic.clinic.entity.MedicalHistory;
import com.clinic.clinic.entity.Specialist;
import org.springframework.stereotype.Component;

@Component
public class ClinicalEvolutionMapper {

    public ClinicalEvolution toEntity(
            ClinicalEvolutionRequest request,
            MedicalHistory medicalHistory,
            Appointment appointment,
            Specialist specialist
    ) {
        ClinicalEvolution evolution = new ClinicalEvolution();
        evolution.setMedicalHistory(medicalHistory);
        evolution.setAppointment(appointment);
        evolution.setSpecialist(specialist);
        evolution.setDiagnosis(request.diagnosis());
        evolution.setTreatment(request.treatment());
        evolution.setNotes(request.notes());
        return evolution;
    }

    public ClinicalEvolutionResponse toResponse(ClinicalEvolution evolution) {
        Long appointmentId = evolution.getAppointment() != null
                ? evolution.getAppointment().getId()
                : null;

        return new ClinicalEvolutionResponse(
                evolution.getId(),
                evolution.getMedicalHistory().getId(),
                evolution.getMedicalHistory().getPatient().getId(),
                appointmentId,
                evolution.getSpecialist().getId(),
                evolution.getDiagnosis(),
                evolution.getTreatment(),
                evolution.getNotes(),
                evolution.getRegisteredAt()
        );
    }
}
