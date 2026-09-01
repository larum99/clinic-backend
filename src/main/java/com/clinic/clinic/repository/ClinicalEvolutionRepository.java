package com.clinic.clinic.repository;

import com.clinic.clinic.entity.ClinicalEvolution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClinicalEvolutionRepository extends JpaRepository<ClinicalEvolution, Long> {
    Page<ClinicalEvolution> findByMedicalHistoryId(Long medicalHistoryId, Pageable pageable);
}
