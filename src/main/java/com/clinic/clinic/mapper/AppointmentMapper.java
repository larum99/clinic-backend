package com.clinic.clinic.mapper;

import com.clinic.clinic.dto.request.AppointmentRequest;
import com.clinic.clinic.dto.response.AppointmentResponse;
import com.clinic.clinic.entity.Appointment;
import com.clinic.clinic.entity.DentalService;
import com.clinic.clinic.entity.Patient;
import com.clinic.clinic.entity.Specialist;
import com.clinic.clinic.entity.User;
import com.clinic.clinic.utils.AppointmentStatus;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public Appointment toEntity(
            AppointmentRequest request,
            Patient patient,
            Specialist specialist,
            DentalService service,
            User createdBy
    ) {

        Appointment appointment = new Appointment();

        appointment.setPatient(patient);
        appointment.setSpecialist(specialist);
        appointment.setService(service);
        appointment.setCreatedBy(createdBy);

        appointment.setStartDatetime(request.startDatetime());

        appointment.setReason(request.reason());
        appointment.setNotes(request.notes());

        appointment.setStatus(AppointmentStatus.PENDIENTE);

        return appointment;
    }

    public AppointmentResponse toResponse(Appointment appointment) {

        Long createdById = appointment.getCreatedBy() != null
                ? appointment.getCreatedBy().getId()
                : null;

        return new AppointmentResponse(
                appointment.getId(),
                appointment.getPatient().getId(),
                appointment.getSpecialist().getId(),
                appointment.getService().getId(),
                appointment.getStatus(),
                appointment.getStartDatetime(),
                appointment.getEndDatetime(),
                appointment.getReason(),
                appointment.getNotes(),
                createdById,
                appointment.getCreatedAt(),
                appointment.getUpdatedAt()
        );
    }
}