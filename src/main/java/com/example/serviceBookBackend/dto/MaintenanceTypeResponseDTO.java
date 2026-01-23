package com.example.serviceBookBackend.dto;

import lombok.Data;

@Data
public class MaintenanceTypeResponseDTO {
    private Integer id;
    private String name;
    private int frequency;
    private Integer carId;
    private boolean isRegular;
}
