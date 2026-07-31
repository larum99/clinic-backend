package com.dental.clinic.service.impl;

import com.dental.clinic.dto.request.PatientRequest;
import com.dental.clinic.dto.response.PatientResponse;
import com.dental.clinic.entity.Patient;
import com.dental.clinic.entity.User;
import com.dental.clinic.exception.DuplicateResourceException;
import com.dental.clinic.exception.ResourceNotFoundException;
import com.dental.clinic.mapper.PatientMapper;
import com.dental.clinic.repository.PatientRepository;
import com.dental.clinic.repository.UserRepository;
import com.dental.clinic.service.PatientService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final PatientMapper patientMapper;

    public PatientServiceImpl(
            PatientRepository patientRepository,
            UserRepository userRepository,
            PatientMapper patientMapper) {

        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.patientMapper = patientMapper;
    }

    @Override
    public PatientResponse createPatient(PatientRequest request) {

        if (patientRepository.existsByDocumentNumber(request.documentNumber())) {
            throw new DuplicateResourceException(
                    "Patient with document number "
                            + request.documentNumber()
                            + " already exists"
            );
        }

        User user = findUserById(request.userId());

        Patient patient = patientMapper.toEntity(request, user);
        Patient savedPatient = patientRepository.save(patient);
        return patientMapper.toResponse(savedPatient);
    }

    @Override
    public PatientResponse findPatientById(Long id) {

        Patient patient = findPatientEntityById(id);
        return patientMapper.toResponse(patient);
    }

    @Override
    public List<PatientResponse> findAllPatients() {

        return patientRepository.findAll()
                .stream()
                .map(patientMapper::toResponse)
                .toList();
    }

    @Override
    public PatientResponse updatePatient(
            Long id,
            PatientRequest request) {

        Patient patient = findPatientEntityById(id);

        User user = findUserById(request.userId());

        if (!patient.getDocumentNumber().equals(request.documentNumber())
                && patientRepository.existsByDocumentNumber(
                request.documentNumber())) {

            throw new DuplicateResourceException(
                    "Patient with document number "
                            + request.documentNumber()
                            + " already exists"
            );
        }

        patient.setUser(user);
        patient.setDocumentType(request.documentType());
        patient.setDocumentNumber(request.documentNumber());
        patient.setBirthDate(request.birthDate());
        patient.setAcceptsData(request.acceptsData());
        patient.setAcceptsPromotions(request.acceptsPromotions());

        Patient updatedPatient = patientRepository.save(patient);

        return patientMapper.toResponse(updatedPatient);
    }

    @Override
    public void deletePatient(Long id) {

        Patient patient = findPatientEntityById(id);

        patientRepository.delete(patient);
    }

    private Patient findPatientEntityById(Long id) {

        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient with id " + id + " was not found"
                ));
    }

    private User findUserById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with id " + id + " was not found"
                ));
    }
}