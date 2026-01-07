package com.example.serviceBookBackend.dto;

import lombok.Data;

@Data
public class MaintenanceTypeCreateDTO {
    private String name;
    private int interval;
    private boolean applyToAllCars;
    private Integer carId;
}
