package com.example.serviceBookBackend.services;

import com.example.serviceBookBackend.dto.JWTResponseDTO;
import com.example.serviceBookBackend.dto.RegisterUserDTO;
import com.example.serviceBookBackend.entity.UserEntity;
import com.example.serviceBookBackend.exceptions.CustomException;
import com.example.serviceBookBackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    @Transactional
    public JWTResponseDTO register(RegisterUserDTO user) {
        Optional<UserEntity> existUser = userRepository.findByEmail(user.getEmail());
        if (existUser.isPresent()) {
            log.info("User already registered: {}", user.getEmail());
            throw new CustomException("Такий користувач вже існує", HttpStatus.CONFLICT);
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());

        UserEntity newUser = new UserEntity();
        newUser.setName(user.getName());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(encodedPassword);
        userRepository.save(newUser);

        String access = jwtService.generateToken(user.getName(), true);
        String refresh = jwtService.generateToken(user.getName(), false);

        JWTResponseDTO tokens = new JWTResponseDTO();
        tokens.setAccess(access);
        tokens.setRefresh(refresh);

        log.info("User {} registered successfully", user.getEmail());

        return tokens;
    }
}
