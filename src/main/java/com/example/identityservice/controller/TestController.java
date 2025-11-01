package com.example.identityservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class TestController {

    @GetMapping("/public")
    public ResponseEntity<?> publicEndpoint() {
        return ResponseEntity.ok("🌍 Bura hamı üçün açıqdır (public)");
    }

    @GetMapping("/user/test")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> userEndpoint() {
        return ResponseEntity.ok("👤 USER endpoint: token var və ROLE_USER olmalıdır");
    }

    @GetMapping("/admin/test")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminEndpoint() {
        return ResponseEntity.ok("🛡️ ADMIN endpoint: yalnız ROLE_ADMIN token ilə girmək olar");
    }
}
