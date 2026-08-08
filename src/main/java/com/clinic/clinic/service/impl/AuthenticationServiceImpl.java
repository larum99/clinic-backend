package com.clinic.clinic.service.impl;

import com.clinic.clinic.dto.request.LoginRequest;
import com.clinic.clinic.dto.response.LoginResponse;
import com.clinic.clinic.dto.response.UserSummaryResponse;
import com.clinic.clinic.entity.User;
import com.clinic.clinic.mapper.UserMapper;
import com.clinic.clinic.repository.UserRepository;
import com.clinic.clinic.security.CustomUserDetails;
import com.clinic.clinic.security.JwtService;
import com.clinic.clinic.service.AuthenticationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    public AuthenticationServiceImpl(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            UserMapper userMapper,
            JwtService jwtService) {

        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
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

        User user = userDetails.getUser();

        user.setLastLogin(LocalDateTime.now());

        userRepository.save(user);

        String token = jwtService.generateToken(userDetails);

        UserSummaryResponse summary =
                userMapper.toSummary(user);

        return new LoginResponse(
                token,
                "Bearer",
                jwtService.getExpiration(),
                summary
        );
    }

}