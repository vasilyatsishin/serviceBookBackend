package com.example.serviceBookBackend.services;

import com.example.serviceBookBackend.entity.JWTEntity;
import com.example.serviceBookBackend.entity.UserEntity;
import com.example.serviceBookBackend.exceptions.CustomException;
import com.example.serviceBookBackend.repository.JWTRepository;
import com.example.serviceBookBackend.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class JWTService {
    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    private Key SIGN_KEY;

    @PostConstruct
    public void init() {
        SIGN_KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateAccessToken(Integer userId) {
        Instant now = Instant.now();
        Instant expiration = now.plus(15, ChronoUnit.MINUTES);

        return Jwts.builder()
                .setSubject(String.valueOf(userId)) // ← ВАЖЛИВО
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(SIGN_KEY)
                .compact();
    }

    public Integer extractUserId(String token) {
        try {
            String subject = Jwts.parserBuilder()
                    .setSigningKey(SIGN_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

            return Integer.parseInt(subject);

        } catch (ExpiredJwtException e) {
            log.warn("Token expired: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isTokenValid(String token, UserEntity userDetails) {
        Integer userId = extractUserId(token);

        return userId != null && userId.equals(userDetails.getId());
    }

    public Integer getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new CustomException("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof Integer) {
            return (Integer) principal;
        } else if (principal instanceof String) {
            try {
                return Integer.parseInt((String) principal);
            } catch (NumberFormatException e) {
                throw new CustomException("Invalid token principal", HttpStatus.UNAUTHORIZED);
            }
        }

        throw new CustomException("Unauthorized", HttpStatus.UNAUTHORIZED);
    }
}
