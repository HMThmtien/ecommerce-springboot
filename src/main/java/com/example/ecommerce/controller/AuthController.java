package com.example.ecommerce.controller;

import com.example.ecommerce.dto.AuthRequest;
import com.example.ecommerce.dto.AuthResponse;
import com.example.ecommerce.dto.RegisterRequest;
import com.example.ecommerce.payload.ApiResponse;
import com.example.ecommerce.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ApiResponse.ok("Đăng ký thành công", null);
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ApiResponse.ok("Đăng nhập thành công", response);
    }
}
