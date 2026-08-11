package com.clinic.clinic.controller;

import com.clinic.clinic.dto.response.PatientDirectoryResponse;
import com.clinic.clinic.service.PatientService;
import com.clinic.clinic.utils.Endpoints;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Endpoints.PATIENTS_DIRECTORY_PATH)
public class PatientDirectoryController {

    private final PatientService patientService;

    public PatientDirectoryController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PACIENTES_VER')")
    public ResponseEntity<List<PatientDirectoryResponse>> findAllPatientsDirectory() {
        List<PatientDirectoryResponse> response =
                patientService.findAllPatientsDirectory();
        return ResponseEntity.ok(response);
    }
}
