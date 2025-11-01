package com.example.identityservice.service;


import com.example.identityservice.dto.*;
import com.example.identityservice.entity.User;
import com.example.identityservice.security.model.Role;
import com.example.identityservice.repository.UserRepository;
import com.example.identityservice.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public void signup(SignupRequest req) {
        String email = req.getEmail().toLowerCase().trim();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Bu email artıq qeydiyyatdan keçib");
        }

        String hashed = passwordEncoder.encode(req.getPassword());
        String code = generateCode();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(10);

        User u = User.builder()
                .email(email)
                .passwordHash(hashed)
                .role(Role.ROLE_USER)
                .isActive(false)
                .verificationCode(code)
                .codeExpiryDate(expiry)
                .build();

        userRepository.save(u);

        // send email (async inside EmailService)
        emailService.sendVerificationCode(email, code);
    }

    @Transactional
    public void verify(VerifyRequest req) {
        String email = req.getEmail().toLowerCase().trim();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("İstifadəçi tapılmadı"));

        if (user.isActive()) {
            return; // already active
        }

        if (user.getVerificationCode() == null ||
                !user.getVerificationCode().equals(req.getCode()) ||
                user.getCodeExpiryDate() == null ||
                user.getCodeExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Yanlış və ya müddəti bitmiş kod");
        }

        user.setActive(true);
        user.setVerificationCode(null);
        user.setCodeExpiryDate(null);
        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest req) {
        String email = req.getEmail().toLowerCase().trim();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email və ya şifrə səhvdir"));

        if (!user.isActive()) throw new IllegalStateException("Hesab aktiv deyil");

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Email və ya şifrə səhvdir");
        }

        String token = jwtUtil.generateToken(user.getEmail(), Collections.singletonList(user.getRole().name()));
        return new LoginResponse(token);
    }

    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    private String generateCode() {
        Random rnd = new Random();
        int number = 100000 + rnd.nextInt(900000);
        return String.valueOf(number);
    }
}

