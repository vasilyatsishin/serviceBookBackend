package com.example.serviceBookBackend.controllers;

import com.example.serviceBookBackend.dto.UserClientDTO;
import com.example.serviceBookBackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UsersController {

    private final UserRepository userRepository;

    @GetMapping("/clients")
    public ResponseEntity<List<UserClientDTO>> getClients() {
        List<UserClientDTO> clients = userRepository.findAllByRole("owner").stream()
                .map(u -> {
                    UserClientDTO dto = new UserClientDTO();
                    dto.setId(u.getId());
                    dto.setName(u.getName());
                    dto.setEmail(u.getEmail());
                    return dto;
                }).toList();
        return ResponseEntity.ok(clients);
    }
}
