package com.clinic.clinic.service.impl;

import com.clinic.clinic.dto.request.ForgotPasswordRequest;
import com.clinic.clinic.dto.request.ResetPasswordRequest;
import com.clinic.clinic.dto.response.ForgotPasswordResponse;
import com.clinic.clinic.dto.response.PasswordResetResponse;
import com.clinic.clinic.entity.PasswordReset;
import com.clinic.clinic.entity.User;
import com.clinic.clinic.exception.BusinessException;
import com.clinic.clinic.exception.ResourceNotFoundException;
import com.clinic.clinic.repository.PasswordResetRepository;
import com.clinic.clinic.repository.UserRepository;
import com.clinic.clinic.service.PasswordResetService;
import com.clinic.clinic.utils.MessageConstants;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
@Transactional(readOnly = true)
public class PasswordResetServiceImpl implements PasswordResetService {

    private static final int TOKEN_EXPIRATION_MINUTES = 30;

    private final PasswordResetRepository passwordResetRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public PasswordResetServiceImpl(
            PasswordResetRepository passwordResetRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.passwordResetRepository = passwordResetRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.USER_NOT_FOUND_EMAIL.formatted(request.email())
                ));

        passwordResetRepository.invalidateActiveTokens(user.getId(), LocalDateTime.now());

        String rawToken = generateRawToken();
        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setUser(user);
        passwordReset.setTokenHash(hashToken(rawToken));
        passwordReset.setExpiresAt(LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_MINUTES));
        passwordResetRepository.save(passwordReset);

        return new ForgotPasswordResponse(
                MessageConstants.PASSWORD_RESET_EMAIL_SENT,
                rawToken
        );
    }

    @Override
    @Transactional
    public PasswordResetResponse resetPassword(ResetPasswordRequest request) {
        PasswordReset passwordReset = passwordResetRepository.findByTokenHash(hashToken(request.token()))
                .orElseThrow(() -> new BusinessException(MessageConstants.PASSWORD_RESET_TOKEN_INVALID));

        if (passwordReset.getUsedAt() != null || passwordReset.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(MessageConstants.PASSWORD_RESET_TOKEN_INVALID);
        }

        User user = passwordReset.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        passwordReset.setUsedAt(LocalDateTime.now());
        passwordResetRepository.save(passwordReset);

        return new PasswordResetResponse(MessageConstants.PASSWORD_RESET_SUCCESS);
    }

    private String generateRawToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
