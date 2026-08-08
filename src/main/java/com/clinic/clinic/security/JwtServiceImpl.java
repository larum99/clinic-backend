package com.clinic.clinic.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtServiceImpl(JwtProperties jwtProperties) {

        this.jwtProperties = jwtProperties;

        this.secretKey = Keys.hmacShaKeyFor(
                jwtProperties.secret()
                        .getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    public String generateToken(CustomUserDetails userDetails) {

        Date issuedAt = new Date();

        Date expiration = new Date(
                issuedAt.getTime()
                        + jwtProperties.expiration()
        );

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    @Override
    public String extractUsername(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    @Override
    public boolean isTokenValid(
            String token,
            CustomUserDetails userDetails) {

        try {

            String username = extractUsername(token);

            return username.equals(userDetails.getUsername())
                    && userDetails.isAccountNonExpired()
                    && userDetails.isAccountNonLocked()
                    && userDetails.isCredentialsNonExpired()
                    && userDetails.isEnabled();

        } catch (Exception exception) {

            return false;
        }
    }

    @Override
    public long getExpiration() {
        return jwtProperties.expiration();
    }

}