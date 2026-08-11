package com.clinic.clinic.service;

import com.clinic.clinic.dto.request.AppointmentRequest;
import com.clinic.clinic.dto.response.AppointmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppointmentService {
    AppointmentResponse createAppointment(AppointmentRequest request, Long createdByUserId);

    AppointmentResponse findAppointmentById(Long id);

    Page<AppointmentResponse> findAllAppointments(Pageable pageable);

    Page<AppointmentResponse> findMyAppointments(Long userId, Pageable pageable);

    AppointmentResponse createMyAppointment(AppointmentRequest request, Long userId);

    AppointmentResponse cancelMyAppointment(Long id, Long userId);

    AppointmentResponse updateAppointment(Long id, AppointmentRequest request, Long changedByUserId);

    void deleteAppointment(Long id);
}
