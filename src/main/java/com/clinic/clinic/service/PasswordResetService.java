package com.clinic.clinic.service;

import com.clinic.clinic.dto.request.ForgotPasswordRequest;
import com.clinic.clinic.dto.request.ResetPasswordRequest;
import com.clinic.clinic.dto.response.ForgotPasswordResponse;
import com.clinic.clinic.dto.response.PasswordResetResponse;

public interface PasswordResetService {
    ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request);

    PasswordResetResponse resetPassword(ResetPasswordRequest request);
}
