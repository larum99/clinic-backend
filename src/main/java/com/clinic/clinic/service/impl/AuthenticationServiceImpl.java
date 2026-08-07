package com.clinic.clinic.service.impl;

import com.clinic.clinic.dto.request.LoginRequest;
import com.clinic.clinic.dto.response.LoginResponse;
import com.clinic.clinic.dto.response.UserSummaryResponse;
import com.clinic.clinic.entity.User;
import com.clinic.clinic.mapper.UserMapper;
import com.clinic.clinic.repository.UserRepository;
import com.clinic.clinic.security.CustomUserDetails;
import com.clinic.clinic.service.AuthenticationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public AuthenticationServiceImpl(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            UserMapper userMapper) {

        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.email(),
                                request.password()
                        )
                );

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        User user = userRepository.findById(
                Objects.requireNonNull(userDetails).getUser().getId()
        ).orElseThrow();

        user.setLastLogin(LocalDateTime.now());

        userRepository.save(user);

        UserSummaryResponse summary =
                userMapper.toSummary(user);

        return new LoginResponse(
                "",
                "Bearer",
                0L,
                summary
        );
    }
}