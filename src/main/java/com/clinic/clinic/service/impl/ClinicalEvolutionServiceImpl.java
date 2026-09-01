package com.clinic.clinic.service.impl;

import com.clinic.clinic.dto.request.ClinicalEvolutionRequest;
import com.clinic.clinic.dto.response.ClinicalEvolutionResponse;
import com.clinic.clinic.entity.Appointment;
import com.clinic.clinic.entity.ClinicalEvolution;
import com.clinic.clinic.entity.MedicalHistory;
import com.clinic.clinic.entity.Specialist;
import com.clinic.clinic.exception.ResourceNotFoundException;
import com.clinic.clinic.mapper.ClinicalEvolutionMapper;
import com.clinic.clinic.repository.AppointmentRepository;
import com.clinic.clinic.repository.ClinicalEvolutionRepository;
import com.clinic.clinic.repository.MedicalHistoryRepository;
import com.clinic.clinic.repository.SpecialistRepository;
import com.clinic.clinic.service.ClinicalEvolutionService;
import com.clinic.clinic.utils.MessageConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ClinicalEvolutionServiceImpl implements ClinicalEvolutionService {

    private final ClinicalEvolutionRepository clinicalEvolutionRepository;
    private final MedicalHistoryRepository medicalHistoryRepository;
    private final SpecialistRepository specialistRepository;
    private final AppointmentRepository appointmentRepository;
    private final ClinicalEvolutionMapper clinicalEvolutionMapper;

    public ClinicalEvolutionServiceImpl(
            ClinicalEvolutionRepository clinicalEvolutionRepository,
            MedicalHistoryRepository medicalHistoryRepository,
            SpecialistRepository specialistRepository,
            AppointmentRepository appointmentRepository,
            ClinicalEvolutionMapper clinicalEvolutionMapper
    ) {
        this.clinicalEvolutionRepository = clinicalEvolutionRepository;
        this.medicalHistoryRepository = medicalHistoryRepository;
        this.specialistRepository = specialistRepository;
        this.appointmentRepository = appointmentRepository;
        this.clinicalEvolutionMapper = clinicalEvolutionMapper;
    }

    @Override
    @Transactional
    public ClinicalEvolutionResponse createClinicalEvolution(ClinicalEvolutionRequest request) {
        MedicalHistory medicalHistory = findMedicalHistoryById(request.medicalHistoryId());
        Specialist specialist = findSpecialistEntityById(request.specialistId());

        Appointment appointment = null;
        if (request.appointmentId() != null) {
            appointment = appointmentRepository.findById(request.appointmentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            MessageConstants.APPOINTMENT_NOT_FOUND.formatted(request.appointmentId())
                    ));
        }

        ClinicalEvolution evolution = clinicalEvolutionMapper.toEntity(
                request, medicalHistory, appointment, specialist
        );
        return clinicalEvolutionMapper.toResponse(clinicalEvolutionRepository.save(evolution));
    }

    @Override
    public ClinicalEvolutionResponse findClinicalEvolutionById(Long id) {
        return clinicalEvolutionMapper.toResponse(findClinicalEvolutionEntityById(id));
    }

    @Override
    public Page<ClinicalEvolutionResponse> findAllClinicalEvolutions(Long medicalHistoryId, Pageable pageable) {
        MedicalHistory medicalHistory = findMedicalHistoryById(medicalHistoryId);
        return clinicalEvolutionRepository
                .findByMedicalHistoryId(medicalHistory.getId(), pageable)
                .map(clinicalEvolutionMapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteClinicalEvolution(Long id) {
        clinicalEvolutionRepository.delete(findClinicalEvolutionEntityById(id));
    }

    private ClinicalEvolution findClinicalEvolutionEntityById(Long id) {
        return clinicalEvolutionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.CLINICAL_EVOLUTION_NOT_FOUND.formatted(id)
                ));
    }

    private MedicalHistory findMedicalHistoryById(Long medicalHistoryId) {
        return medicalHistoryRepository.findById(medicalHistoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.MEDICAL_HISTORY_NOT_FOUND.formatted(medicalHistoryId)
                ));
    }

    private Specialist findSpecialistEntityById(Long id) {
        return specialistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.SPECIALIST_NOT_FOUND.formatted(id)
                ));
    }
}
