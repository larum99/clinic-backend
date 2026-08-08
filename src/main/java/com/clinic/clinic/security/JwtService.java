package com.clinic.clinic.security;

public interface JwtService {
    String generateToken(CustomUserDetails userDetails);
    String extractUsername(String token);
    boolean isTokenValid(String token, CustomUserDetails userDetails);
    long getExpiration();
}
