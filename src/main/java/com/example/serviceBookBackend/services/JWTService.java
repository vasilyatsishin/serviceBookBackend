package com.example.serviceBookBackend.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

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

    public String generateToken(String username, boolean isAccess) {
        Instant now = Instant.now();
        Instant expiration = isAccess
                ? now.plus(15, ChronoUnit.MINUTES)
                : now.plus(30, ChronoUnit.DAYS);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(SIGN_KEY)
                .compact();
    }
}
