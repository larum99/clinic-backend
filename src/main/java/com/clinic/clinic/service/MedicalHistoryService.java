package com.clinic.clinic.service;

import com.clinic.clinic.dto.request.MedicalHistoryRequest;
import com.clinic.clinic.dto.response.MedicalHistoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MedicalHistoryService {
    MedicalHistoryResponse createMedicalHistory(MedicalHistoryRequest request);

    MedicalHistoryResponse findMedicalHistoryById(Long id);

    MedicalHistoryResponse findMedicalHistoryByPatientId(Long patientId);

    Page<MedicalHistoryResponse> findAllMedicalHistories(Pageable pageable);

    MedicalHistoryResponse updateMedicalHistory(Long id, MedicalHistoryRequest request);

    void deleteMedicalHistory(Long id);
}
