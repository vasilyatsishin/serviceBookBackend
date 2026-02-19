package com.example.serviceBookBackend.dto;

import lombok.Data;

@Data
public class JWTResponseDTO {
    private String access;
    private String refresh;
}
