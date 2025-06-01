package com.shauryaORG.NoCheatCode.controller;

import com.shauryaORG.NoCheatCode.dto.auth.AuthResponse;
import com.shauryaORG.NoCheatCode.dto.auth.LoginRequest;
import com.shauryaORG.NoCheatCode.dto.auth.RegisterRequest;
import com.shauryaORG.NoCheatCode.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        request.sanitize();
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        request.sanitize();
        return ResponseEntity.ok(authService.login(request));
    }
}

