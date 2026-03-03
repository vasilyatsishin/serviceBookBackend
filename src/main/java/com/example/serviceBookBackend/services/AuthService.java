package com.example.serviceBookBackend.services;

import com.example.serviceBookBackend.dto.JWTResponseDTO;
import com.example.serviceBookBackend.dto.LoginRequestDTO;
import com.example.serviceBookBackend.dto.RegisterUserDTO;
import com.example.serviceBookBackend.entity.JWTEntity;
import com.example.serviceBookBackend.entity.UserEntity;
import com.example.serviceBookBackend.exceptions.CustomException;
import com.example.serviceBookBackend.repository.JWTRepository;
import com.example.serviceBookBackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final JWTRepository jWTRepository;

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

        String access = jwtService.generateAccessToken(newUser.getId());
        String refresh = UUID.randomUUID().toString();

        JWTResponseDTO tokens = new JWTResponseDTO();
        tokens.setAccess(access);
        tokens.setRefresh(refresh);

        JWTEntity refreshTokenEntity = new JWTEntity();
        refreshTokenEntity.setUserId(newUser);
        refreshTokenEntity.setToken(refresh);
        refreshTokenEntity.setExpiryDate(LocalDateTime.now().plusDays(30));
        jWTRepository.save(refreshTokenEntity);

        log.info("User {} registered successfully", user.getEmail());

        return tokens;
    }

    public String refreshToken(String refreshToken) {
        // 1. Шукаємо токен у таблиці рефреш-токенів
        log.info("Refreshing token: {}", refreshToken);
        JWTEntity tokenEntity = jWTRepository.findByToken(refreshToken).
                orElseThrow(() -> new CustomException("Токен не знайдено", HttpStatus.UNAUTHORIZED));

        // 2. Перевіряємо, чи не протух токен у БАЗІ (по даті)
        if (tokenEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
            jWTRepository.delete(tokenEntity);
            throw new CustomException("Рефреш токен протух", HttpStatus.UNAUTHORIZED);
        }

        // 3. Дістаємо юзера
        UserEntity user = tokenEntity.getUserId(); // Переконайся, що тут не null
        if (user == null) {
            throw new CustomException("Користувача не знайдено", HttpStatus.NOT_FOUND);
        }

        // 4. Генеруємо новий Access токен
        return jwtService.generateAccessToken(user.getId());
    }

    @Transactional
    public JWTResponseDTO login(LoginRequestDTO request) {
        // 1. Шукаємо юзера за email
        UserEntity user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new CustomException("Невірний email або пароль", HttpStatus.NOT_FOUND));

        // 2. Перевіряємо пароль
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException("Невірний email або пароль", HttpStatus.NOT_FOUND);
        }

        // 3. Генеруємо токени
        String access = jwtService.generateAccessToken(user.getId());
        String refresh = UUID.randomUUID().toString(); // Твій новий підхід з UUID

        // 4. Оновлюємо або створюємо запис у таблиці токенів
        // Оскільки в тебе OneToOne, краще спочатку видалити старий токен, якщо він є
        jWTRepository.deleteByUserId(user);

        JWTEntity refreshTokenEntity = new JWTEntity();
        refreshTokenEntity.setUserId(user);
        refreshTokenEntity.setToken(refresh);
        refreshTokenEntity.setExpiryDate(LocalDateTime.now().plusDays(30));
        jWTRepository.save(refreshTokenEntity);

        JWTResponseDTO response = new JWTResponseDTO();
        response.setAccess(access);
        response.setRefresh(refresh);

        return response;
    }

    @Transactional
    public void logout(String refreshToken) {
        jWTRepository.deleteByToken(refreshToken);
    }
}
