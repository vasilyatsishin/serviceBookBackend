package com.example.serviceBookBackend.dto;

import lombok.Data;

@Data
public class MaintenanceTypeCreateDTO {
    private Integer catalogId;
    private int interval;
    private boolean applyToAllCars;
    private Integer carId;
    private boolean regular;
}
