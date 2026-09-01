package com.clinic.clinic.controller;

import com.clinic.clinic.dto.request.ClinicalEvolutionRequest;
import com.clinic.clinic.dto.response.ClinicalEvolutionResponse;
import com.clinic.clinic.service.ClinicalEvolutionService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.CLINICAL_EVOLUTIONS_PATH)
public class ClinicalEvolutionController {

    private final ClinicalEvolutionService clinicalEvolutionService;

    public ClinicalEvolutionController(ClinicalEvolutionService clinicalEvolutionService) {
        this.clinicalEvolutionService = clinicalEvolutionService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ESPECIALISTA')")
    public ResponseEntity<ClinicalEvolutionResponse> createClinicalEvolution(
            @Valid @RequestBody ClinicalEvolutionRequest request) {
        ClinicalEvolutionResponse response = clinicalEvolutionService.createClinicalEvolution(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ESPECIALISTA')")
    public ResponseEntity<Page<ClinicalEvolutionResponse>> findAllClinicalEvolutions(
            @RequestParam Long medicalHistoryId,
            @PageableDefault(
                    size = PaginationConstants.DEFAULT_PAGE_SIZE,
                    sort = PaginationConstants.DEFAULT_SORT_BY
            )
            Pageable pageable) {
        Page<ClinicalEvolutionResponse> response =
                clinicalEvolutionService.findAllClinicalEvolutions(medicalHistoryId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ESPECIALISTA')")
    public ResponseEntity<ClinicalEvolutionResponse> findClinicalEvolutionById(@PathVariable Long id) {
        ClinicalEvolutionResponse response = clinicalEvolutionService.findClinicalEvolutionById(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteClinicalEvolution(@PathVariable Long id) {
        clinicalEvolutionService.deleteClinicalEvolution(id);
        return ResponseEntity.noContent().build();
    }
}
