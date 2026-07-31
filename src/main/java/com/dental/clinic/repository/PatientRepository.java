package com.dental.clinic.repository;

import com.dental.clinic.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    boolean existsByDocumentNumber(String documentNumber);
}