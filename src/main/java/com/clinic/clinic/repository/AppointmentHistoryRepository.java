package com.clinic.clinic.repository;

import com.clinic.clinic.entity.AppointmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentHistoryRepository extends JpaRepository<AppointmentHistory, Long> {
    List<AppointmentHistory> findByAppointmentId(Long appointmentId);
}
