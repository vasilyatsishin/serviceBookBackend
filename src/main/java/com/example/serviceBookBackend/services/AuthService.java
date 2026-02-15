package com.example.serviceBookBackend.services;

import com.example.serviceBookBackend.dto.RegisterUserDTO;
import com.example.serviceBookBackend.entity.UserEntity;
import com.example.serviceBookBackend.exceptions.CustomException;
import com.example.serviceBookBackend.repository.UserRepository;
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

    public void register(RegisterUserDTO user) {
        Optional<UserEntity> existUser = userRepository.findByEmail(user.getEmail());
        if (existUser.isPresent()) {
            throw new CustomException("Такий користувач вже існує", HttpStatus.CONFLICT);
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());

        UserEntity newUser = new UserEntity();
        newUser.setName(user.getName());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(encodedPassword);
        userRepository.save(newUser);
        log.info("User {} registered successfully", user.getEmail());
    }
}
