package com.clinic.clinic.service.impl;

import com.clinic.clinic.config.PasswordResetProperties;
import com.clinic.clinic.dto.request.ForgotPasswordRequest;
import com.clinic.clinic.dto.request.ResetPasswordRequest;
import com.clinic.clinic.dto.response.ForgotPasswordResponse;
import com.clinic.clinic.dto.response.PasswordResetResponse;
import com.clinic.clinic.entity.PasswordReset;
import com.clinic.clinic.entity.User;
import com.clinic.clinic.exception.BusinessException;
import com.clinic.clinic.repository.PasswordResetRepository;
import com.clinic.clinic.repository.UserRepository;
import com.clinic.clinic.service.EmailService;
import com.clinic.clinic.service.PasswordResetService;
import com.clinic.clinic.utils.MessageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(PasswordResetServiceImpl.class);

    private final PasswordResetRepository passwordResetRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PasswordResetProperties properties;
    private final SecureRandom secureRandom = new SecureRandom();

    public PasswordResetServiceImpl(
            PasswordResetRepository passwordResetRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService,
            PasswordResetProperties properties) {

        this.passwordResetRepository = passwordResetRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.properties = properties;
    }

    @Override
    @Transactional
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {

        userRepository.findByEmail(request.email()).ifPresent(user -> {
            passwordResetRepository.invalidateActiveTokens(user.getId(), LocalDateTime.now());

            String rawToken = generateRawToken();
            PasswordReset passwordReset = new PasswordReset();
            passwordReset.setUser(user);
            passwordReset.setTokenHash(hashToken(rawToken));
            passwordReset.setExpiresAt(
                    LocalDateTime.now().plusMinutes(properties.getExpirationMinutes())
            );
            passwordResetRepository.save(passwordReset);

            String resetLink = buildResetLink(rawToken);
            sendResetEmail(user, resetLink);
        });

        return new ForgotPasswordResponse(MessageConstants.PASSWORD_RESET_EMAIL_SENT);
    }

    @Override
    @Transactional
    public PasswordResetResponse resetPassword(ResetPasswordRequest request) {

        PasswordReset passwordReset = passwordResetRepository.findByTokenHash(hashToken(request.token()))
                .orElseThrow(() -> new BusinessException(MessageConstants.PASSWORD_RESET_TOKEN_INVALID));

        if (passwordReset.getUsedAt() != null
                || passwordReset.getExpiresAt().isBefore(LocalDateTime.now())) {

            throw new BusinessException(MessageConstants.PASSWORD_RESET_TOKEN_INVALID);
        }

        User user = passwordReset.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        passwordReset.setUsedAt(LocalDateTime.now());
        passwordResetRepository.save(passwordReset);

        return new PasswordResetResponse(MessageConstants.PASSWORD_RESET_SUCCESS);
    }

    private void sendResetEmail(User user, String resetLink) {

        try {
            emailService.sendPasswordResetEmail(
                    user.getEmail(),
                    resetLink,
                    user.getFirstName()
            );
        } catch (RuntimeException e) {
            log.error("Could not deliver password reset email for user id {}", user.getId(), e);
        }
    }

    private String buildResetLink(String rawToken) {

        String frontendUrl = properties.getFrontendUrl();
        String separator = frontendUrl.endsWith("/") ? "" : "/";
        return frontendUrl + separator + "reset-password?token=" + rawToken;
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
