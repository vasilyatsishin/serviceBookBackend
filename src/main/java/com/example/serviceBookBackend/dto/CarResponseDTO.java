package com.example.serviceBookBackend.dto;

import lombok.Data;

@Data
public class CarResponseDTO {
    private Integer id;
    private String name;
    private int odometer;
    private String photoUrl;
}
