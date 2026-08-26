package com.clinic.clinic.controller;

import com.clinic.clinic.dto.request.ForgotPasswordRequest;
import com.clinic.clinic.dto.request.LoginRequest;
import com.clinic.clinic.dto.request.RegisterRequest;
import com.clinic.clinic.dto.request.ResetPasswordRequest;
import com.clinic.clinic.dto.response.ForgotPasswordResponse;
import com.clinic.clinic.dto.response.LoginResponse;
import com.clinic.clinic.dto.response.PasswordResetResponse;
import com.clinic.clinic.dto.response.RegisterResponse;
import com.clinic.clinic.service.AuthenticationService;
import com.clinic.clinic.service.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final PasswordResetService passwordResetService;

    public AuthenticationController(
            AuthenticationService authenticationService,
            PasswordResetService passwordResetService) {

        this.authenticationService = authenticationService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                authenticationService.login(request)
        );
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authenticationService.register(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        return ResponseEntity.ok(
                passwordResetService.forgotPassword(request)
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<PasswordResetResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        return ResponseEntity.ok(
                passwordResetService.resetPassword(request)
        );
    }
}