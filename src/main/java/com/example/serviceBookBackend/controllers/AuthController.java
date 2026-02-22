package com.example.serviceBookBackend.controllers;

import com.example.serviceBookBackend.dto.JWTResponseDTO;
import com.example.serviceBookBackend.dto.LoginRequestDTO;
import com.example.serviceBookBackend.dto.RegisterUserDTO;
import com.example.serviceBookBackend.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterUserDTO user) {
        log.info("Received request to register user: {}", user.getEmail());

        JWTResponseDTO tokens = authService.register(user);

        ResponseCookie cookie = ResponseCookie.from("refresh", tokens.getRefresh()).httpOnly(true).secure(false) // Для дева на localhost ставимо false, на проді (https) має бути true
                .path("/").maxAge(30 * 24 * 60 * 60) // 30 днів
                .sameSite("Lax").build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(tokens.getAccess());
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(@CookieValue(name = "refresh", required = false) String refreshToken) {
        log.info("Received request to refresh token");
        if (refreshToken == null) {
            log.warn("Refresh attempt without cookie");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String newAccess = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(newAccess);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        log.info("Received request to login user: {}", loginRequest.getEmail());
        JWTResponseDTO tokens = authService.login(loginRequest);

        ResponseCookie cookie = ResponseCookie.from("refresh", tokens.getRefresh())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(30 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();

        log.info("User logged in: {}", loginRequest.getEmail());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(tokens.getAccess());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = "refresh", required = false) String refreshToken // Spring сам дістане куку!
    ) {
        log.info("Logout request received. Token present: {}", refreshToken != null);

        if (refreshToken != null) {
            authService.logout(refreshToken);
        }

        // Видаляємо куку з браузера (overwrite)
        ResponseCookie cookie = ResponseCookie.from("refresh", "").httpOnly(true).secure(false).path("/").maxAge(0).sameSite("Lax").build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }
}
