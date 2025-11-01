package com.example.identityservice.security.service;


import com.example.identityservice.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtTokenValidator {
    private final JwtUtil jwtUtil;

    public boolean isValid(String token) {
        return jwtUtil.validateToken(token);
    }

    public String getEmailFromToken(String token) {
        return jwtUtil.extractEmail(token);
    }
}

