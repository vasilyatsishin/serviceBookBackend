package com.example.serviceBookBackend.controllers;

import com.example.serviceBookBackend.dto.RegisterUserDTO;
import com.example.serviceBookBackend.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public void login(@Valid @RequestBody RegisterUserDTO user) {
        log.info("Received request to register user: {}", user.getEmail());
        authService.register(user);
    }
}
