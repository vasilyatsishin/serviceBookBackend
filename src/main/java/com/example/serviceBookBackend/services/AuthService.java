package com.example.serviceBookBackend.services;

import com.example.serviceBookBackend.dto.RegisterUserDTO;
import com.example.serviceBookBackend.entity.UserEntity;
import com.example.serviceBookBackend.exceptions.CustomException;
import com.example.serviceBookBackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    public void register(RegisterUserDTO user) {
        Optional<UserEntity> userEntity = userRepository.findByEmail(user.getEmail());
        if (userEntity.isPresent()) {
            throw new CustomException("Такий користувач вже існує", HttpStatus.CONFLICT);
        }
    }
}
