package com.clinic.clinic.service.impl;

import com.clinic.clinic.dto.request.LoginRequest;
import com.clinic.clinic.dto.request.RegisterRequest;
import com.clinic.clinic.dto.response.LoginResponse;
import com.clinic.clinic.dto.response.RegisterResponse;
import com.clinic.clinic.dto.response.UserSummaryResponse;
import com.clinic.clinic.entity.Patient;
import com.clinic.clinic.entity.Role;
import com.clinic.clinic.entity.User;
import com.clinic.clinic.enums.UserStatus;
import com.clinic.clinic.exception.DuplicateResourceException;
import com.clinic.clinic.exception.ResourceNotFoundException;
import com.clinic.clinic.mapper.UserMapper;
import com.clinic.clinic.repository.PatientRepository;
import com.clinic.clinic.repository.RoleRepository;
import com.clinic.clinic.repository.UserRepository;
import com.clinic.clinic.security.CustomUserDetails;
import com.clinic.clinic.security.JwtService;
import com.clinic.clinic.service.AuthenticationService;
import com.clinic.clinic.utils.MessageConstants;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final String PATIENT_ROLE_NAME = "PACIENTE";

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PatientRepository patientRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthenticationServiceImpl(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PatientRepository patientRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.patientRepository = patientRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
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

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        validateEmailDoesNotExist(request.email());

        validateDocumentNumberDoesNotExist(request.documentNumber());

        Role patientRole = roleRepository.findByName(PATIENT_ROLE_NAME)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageConstants.ROLE_NOT_FOUND_BY_NAME
                                .formatted(PATIENT_ROLE_NAME)
                ));

        User user = new User();

        user.setRole(patientRole);
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setStatus(UserStatus.ACTIVO);
        user.setPasswordHash(
                passwordEncoder.encode(request.password())
        );

        User savedUser = userRepository.save(user);

        Patient patient = new Patient();

        patient.setUser(savedUser);
        patient.setDocumentType(request.documentType());
        patient.setDocumentNumber(request.documentNumber());
        patient.setBirthDate(request.birthDate());
        patient.setAcceptsData(false);
        patient.setAcceptsPromotions(false);

        patientRepository.save(patient);

        return new RegisterResponse(
                MessageConstants.REGISTER_SUCCESS
        );
    }

    private void validateEmailDoesNotExist(String email) {

        if (userRepository.existsByEmail(email)) {

            throw new DuplicateResourceException(
                    MessageConstants.EMAIL_ALREADY_EXISTS.formatted(email)
            );
        }
    }

    private void validateDocumentNumberDoesNotExist(String documentNumber) {

        if (patientRepository.existsByDocumentNumber(documentNumber)) {

            throw new DuplicateResourceException(
                    MessageConstants.PATIENT_DOCUMENT_DUPLICATE
                            .formatted(documentNumber)
            );
        }
    }

}