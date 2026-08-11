package com.clinic.clinic.service;

import com.clinic.clinic.dto.request.LoginRequest;
import com.clinic.clinic.dto.request.RegisterRequest;
import com.clinic.clinic.dto.response.LoginResponse;
import com.clinic.clinic.dto.response.RegisterResponse;

public interface AuthenticationService {

    LoginResponse login(LoginRequest request);

    RegisterResponse register(RegisterRequest request);

}