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

    public String generateAccessToken(String username) {
        Instant now = Instant.now();
        Instant expiration = now.plus(15, ChronoUnit.MINUTES);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(SIGN_KEY)
                .compact();
    }

    public String extractUsername(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SIGN_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            log.warn("Token expired: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isTokenValid(String token, UserEntity userDetails) {
        try {
            final String username = extractUsername(token);
            // Якщо extractUsername повернув null (через ExpiredJwtException), то токен невалідний
            return (username != null && username.equals(userDetails.getEmail()));
        } catch (Exception e) {
            return false;
        }
    }
}
