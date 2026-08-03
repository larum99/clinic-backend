package com.clinic.clinic.repository;

import com.clinic.clinic.entity.SpecialistSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpecialistScheduleRepository extends JpaRepository<SpecialistSchedule, Long> {
    List<SpecialistSchedule> findBySpecialistIdAndDayOfWeek(
            Long specialistId,
            Short dayOfWeek
    );
}