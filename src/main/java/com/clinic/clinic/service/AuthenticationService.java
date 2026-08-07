package com.clinic.clinic.service;

import com.clinic.clinic.dto.request.LoginRequest;
import com.clinic.clinic.dto.response.LoginResponse;

public interface AuthenticationService {

    LoginResponse login(LoginRequest request);

}