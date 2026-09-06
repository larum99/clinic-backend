package com.clinic.clinic.controller;

import com.clinic.clinic.dto.request.MedicalHistoryRequest;
import com.clinic.clinic.dto.response.MedicalHistoryResponse;
import com.clinic.clinic.service.MedicalHistoryService;
import com.clinic.clinic.utils.Endpoints;
import com.clinic.clinic.utils.PaginationConstants;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.MEDICAL_HISTORIES_PATH)
public class MedicalHistoryController {

    private final MedicalHistoryService medicalHistoryService;

    public MedicalHistoryController(MedicalHistoryService medicalHistoryService) {
        this.medicalHistoryService = medicalHistoryService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ESPECIALISTA')")
    public ResponseEntity<MedicalHistoryResponse> createMedicalHistory(
            @Valid @RequestBody MedicalHistoryRequest request) {
        MedicalHistoryResponse response = medicalHistoryService.createMedicalHistory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<MedicalHistoryResponse>> findAllMedicalHistories(
            @PageableDefault(
                    size = PaginationConstants.DEFAULT_PAGE_SIZE,
                    sort = PaginationConstants.DEFAULT_SORT_BY
            )
            Pageable pageable) {
        Page<MedicalHistoryResponse> response = medicalHistoryService.findAllMedicalHistories(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ESPECIALISTA')")
    public ResponseEntity<MedicalHistoryResponse> findMedicalHistoryByPatientId(
            @PathVariable Long patientId) {
        MedicalHistoryResponse response = medicalHistoryService.findMedicalHistoryByPatientId(patientId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ESPECIALISTA')")
    public ResponseEntity<MedicalHistoryResponse> findMedicalHistoryById(@PathVariable Long id) {
        MedicalHistoryResponse response = medicalHistoryService.findMedicalHistoryById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ESPECIALISTA')")
    public ResponseEntity<MedicalHistoryResponse> updateMedicalHistory(
            @PathVariable Long id,
            @Valid @RequestBody MedicalHistoryRequest request) {
        MedicalHistoryResponse response = medicalHistoryService.updateMedicalHistory(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMedicalHistory(@PathVariable Long id) {
        medicalHistoryService.deleteMedicalHistory(id);
        return ResponseEntity.noContent().build();
    }
}
