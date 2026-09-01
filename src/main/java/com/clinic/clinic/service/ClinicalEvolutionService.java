package com.clinic.clinic.service;

import com.clinic.clinic.dto.request.ClinicalEvolutionRequest;
import com.clinic.clinic.dto.response.ClinicalEvolutionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClinicalEvolutionService {
    ClinicalEvolutionResponse createClinicalEvolution(ClinicalEvolutionRequest request);

    ClinicalEvolutionResponse findClinicalEvolutionById(Long id);

    Page<ClinicalEvolutionResponse> findAllClinicalEvolutions(Long medicalHistoryId, Pageable pageable);

    void deleteClinicalEvolution(Long id);
}
