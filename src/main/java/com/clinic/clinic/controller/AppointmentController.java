package com.clinic.clinic.controller;

import com.clinic.clinic.dto.request.AppointmentRequest;
import com.clinic.clinic.dto.response.AppointmentResponse;
import com.clinic.clinic.security.CustomUserDetails;
import com.clinic.clinic.service.AppointmentService;
import com.clinic.clinic.utils.Endpoints;
import com.clinic.clinic.utils.PaginationConstants;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.APPOINTMENTS_PATH)
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIO', 'PACIENTE')")
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody AppointmentRequest request,
            Authentication authentication) {
        AppointmentResponse response = appointmentService.createAppointment(
                request,
                currentUserId(authentication)
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIO')")
    public ResponseEntity<Page<AppointmentResponse>> findAllAppointments(
            @PageableDefault(
                    size = PaginationConstants.DEFAULT_PAGE_SIZE,
                    sort = PaginationConstants.DEFAULT_SORT_BY
            )
            Pageable pageable) {
        Page<AppointmentResponse> response = appointmentService.findAllAppointments(pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/mine")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<AppointmentResponse> createMyAppointment(
            @Valid @RequestBody AppointmentRequest request,
            Authentication authentication) {
        AppointmentResponse response = appointmentService.createMyAppointment(
                request,
                currentUserId(authentication)
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/mine")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIO', 'PACIENTE')")
    public ResponseEntity<Page<AppointmentResponse>> findMyAppointments(
            @PageableDefault(
                    size = PaginationConstants.DEFAULT_PAGE_SIZE,
                    sort = PaginationConstants.DEFAULT_SORT_BY
            )
            Pageable pageable,
            Authentication authentication) {
        Page<AppointmentResponse> response = appointmentService.findMyAppointments(
                currentUserId(authentication),
                pageable
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/mine/{id}")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<AppointmentResponse> cancelMyAppointment(
            @PathVariable Long id,
            Authentication authentication) {
        AppointmentResponse response = appointmentService.cancelMyAppointment(
                id,
                currentUserId(authentication)
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIO')")
    public ResponseEntity<AppointmentResponse> findAppointmentById(@PathVariable Long id) {
        AppointmentResponse response = appointmentService.findAppointmentById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIO')")
    public ResponseEntity<AppointmentResponse> updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentRequest request,
            Authentication authentication) {
        AppointmentResponse response = appointmentService.updateAppointment(
                id,
                request,
                currentUserId(authentication)
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    private Long currentUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            return null;
        }
        return userDetails.getUser().getId();
    }
}
