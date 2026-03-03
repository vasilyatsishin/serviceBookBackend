package com.example.serviceBookBackend.security;

import com.example.serviceBookBackend.entity.UserEntity;
import com.example.serviceBookBackend.exceptions.CustomException;
import com.example.serviceBookBackend.repository.UserRepository;
import com.example.serviceBookBackend.services.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JWTAuthFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserRepository userRepository; // Твій сервіс для завантаження юзера з БД

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        Integer userId = jwtService.extractUserId(jwt);

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Перевіряємо, чи токен валідний
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException("Користувача не знайдено", HttpStatus.UNAUTHORIZED));

            if (jwtService.isTokenValid(jwt, user)) {

                // 🔹 Кладемо userId як principal
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                user.getId(),  // 👈 тут тільки ID
                                null,
                                List.of()      // ролі
                        );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}