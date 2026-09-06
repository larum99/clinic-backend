package com.clinic.clinic.service.impl;

import com.clinic.clinic.dto.request.MedicalHistoryRequest;
import com.clinic.clinic.dto.response.MedicalHistoryResponse;
import com.clinic.clinic.entity.MedicalHistory;
import com.clinic.clinic.entity.Patient;
import com.clinic.clinic.exception.BusinessException;
import com.clinic.clinic.exception.DuplicateResourceException;
import com.clinic.clinic.exception.ResourceNotFoundException;
import com.clinic.clinic.mapper.MedicalHistoryMapper;
import com.clinic.clinic.repository.MedicalHistoryRepository;
import com.clinic.clinic.repository.PatientRepository;
import com.clinic.clinic.service.MedicalHistoryService;
import com.clinic.clinic.utils.MessageConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MedicalHistoryServiceImpl implements MedicalHistoryService {

    private final MedicalHistoryRepository medicalHistoryRepository;
    private final PatientRepository patientRepository;
    private final MedicalHistoryMapper medicalHistoryMapper;

    public MedicalHistoryServiceImpl(
            MedicalHistoryRepository medicalHistoryRepository,
            PatientRepository patientRepository,
            MedicalHistoryMapper medicalHistoryMapper
    ) {
        this.medicalHistoryRepository = medicalHistoryRepository;
        this.patientRepository = patientRepository;
        this.medicalHistoryMapper = medicalHistoryMapper;
    }

    @Override
    @Transactional
    public MedicalHistoryResponse createMedicalHistory(MedicalHistoryRequest request) {
        Patient patient = findPatientEntityById(request.patientId());

        if (medicalHistoryRepository.existsByPatientId(patient.getId())) {
            throw new DuplicateResourceException(
                    MessageConstants.MEDICAL_HISTORY_ALREADY_EXISTS.formatted(patient.getId())
            );
        }

        MedicalHistory medicalHistory = medicalHistoryMapper.toEntity(request, patient);
        return medicalHistoryMapper.toResponse(medicalHistoryRepository.save(medicalHistory));
    }

    @Override
    public MedicalHistoryResponse findMedicalHistoryById(Long id) {
        return medicalHistoryMapper.toResponse(findMedicalHistoryEntityById(id));
    }

    @Override
    public MedicalHistoryResponse findMedicalHistoryByPatientId(Long patientId) {
        MedicalHistory medicalHistory = medicalHistoryRepository.findByPatientId(patientId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.MEDICAL_HISTORY_NOT_FOUND_FOR_PATIENT.formatted(patientId)
                ));
        return medicalHistoryMapper.toResponse(medicalHistory);
    }

    @Override
    public Page<MedicalHistoryResponse> findAllMedicalHistories(Pageable pageable) {
        return medicalHistoryRepository.findAll(pageable).map(medicalHistoryMapper::toResponse);
    }

    @Override
    @Transactional
    public MedicalHistoryResponse updateMedicalHistory(Long id, MedicalHistoryRequest request) {
        MedicalHistory medicalHistory = findMedicalHistoryEntityById(id);

        if (!medicalHistory.getPatient().getId().equals(request.patientId())) {
            Patient patient = findPatientEntityById(request.patientId());
            if (medicalHistoryRepository.existsByPatientId(patient.getId())) {
                throw new BusinessException(
                        MessageConstants.MEDICAL_HISTORY_ALREADY_EXISTS.formatted(patient.getId())
                );
            }
            medicalHistory.setPatient(patient);
        }

        medicalHistoryMapper.updateEntity(medicalHistory, request);
        return medicalHistoryMapper.toResponse(medicalHistoryRepository.save(medicalHistory));
    }

    @Override
    @Transactional
    public void deleteMedicalHistory(Long id) {
        medicalHistoryRepository.delete(findMedicalHistoryEntityById(id));
    }

    private MedicalHistory findMedicalHistoryEntityById(Long id) {
        return medicalHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.MEDICAL_HISTORY_NOT_FOUND.formatted(id)
                ));
    }

    private Patient findPatientEntityById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.PATIENT_NOT_FOUND.formatted(id)
                ));
    }
}
