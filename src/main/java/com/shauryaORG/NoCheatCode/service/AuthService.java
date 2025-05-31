package com.shauryaORG.NoCheatCode.service;

import com.shauryaORG.NoCheatCode.dto.auth.AuthResponse;
import com.shauryaORG.NoCheatCode.dto.auth.LoginRequest;
import com.shauryaORG.NoCheatCode.dto.auth.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}