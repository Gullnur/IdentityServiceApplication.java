package com.example.identityservice.controller;


import com.example.identityservice.dto.*;
import com.example.identityservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest req) {
        authService.signup(req);
        return ResponseEntity.ok(new MessageResponse("Təsdiq kodu emailinizə göndərildi"));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerifyRequest req) {
        authService.verify(req);
        return ResponseEntity.ok(new MessageResponse("Hesabınız aktiv edildi. İndi daxil ola bilərsiniz"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        LoginResponse resp = authService.login(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validate(@RequestBody TokenValidateRequest req) {
        boolean ok = authService.validateToken(req.getToken());
        return ResponseEntity.ok(new TokenValidateResponse(ok));
    }
}

