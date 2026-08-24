package com.clinic.clinic.controller;

import com.clinic.clinic.dto.request.PatientRequest;
import com.clinic.clinic.dto.response.PatientResponse;
import com.clinic.clinic.dto.response.PatientSummaryResponse;
import com.clinic.clinic.service.PatientService;
import com.clinic.clinic.utils.Endpoints;
import com.clinic.clinic.utils.PaginationConstants;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Endpoints.PATIENTS_PATH)
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PACIENTES_GESTIONAR')")
    public ResponseEntity<PatientResponse> createPatient(
            @Valid @RequestBody PatientRequest request) {
        PatientResponse response = patientService.createPatient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PACIENTES_VER')")
    public ResponseEntity<Page<PatientResponse>> findAllPatients(
            @PageableDefault(size = PaginationConstants.DEFAULT_PAGE_SIZE,
                    sort = PaginationConstants.DEFAULT_SORT_BY) Pageable pageable) {
        Page<PatientResponse> response = patientService.findAllPatients(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/directory")
    @PreAuthorize("hasAuthority('PACIENTES_VER')")
    public ResponseEntity<List<PatientSummaryResponse>> findAllPatientsSummary() {
        List<PatientSummaryResponse> response =
                patientService.findAllPatientsSummary();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PACIENTES_VER')")
    public ResponseEntity<PatientResponse> findPatientById(@PathVariable Long id) {
        PatientResponse response = patientService.findPatientById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PACIENTES_GESTIONAR')")
    public ResponseEntity<PatientResponse> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientRequest request) {
        PatientResponse response = patientService.updatePatient(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
