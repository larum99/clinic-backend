package com.clinic.clinic.service;

import com.clinic.clinic.dto.request.PatientRequest;
import com.clinic.clinic.dto.response.PatientResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PatientService {
    PatientResponse createPatient(PatientRequest request);
    PatientResponse findPatientById(Long id);
    Page<PatientResponse> findAllPatients(Pageable pageable);
    PatientResponse updatePatient(Long id, PatientRequest request);
    void deletePatient(Long id);
}