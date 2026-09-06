package com.clinic.clinic.repository;

import com.clinic.clinic.entity.MedicalHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, Long> {
    boolean existsByPatientId(Long patientId);
    Optional<MedicalHistory> findByPatientId(Long patientId);
}
