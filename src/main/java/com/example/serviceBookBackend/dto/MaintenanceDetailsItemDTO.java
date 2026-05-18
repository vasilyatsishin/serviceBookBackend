package com.example.serviceBookBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MaintenanceDetailsItemDTO {
    private String name;
    private double price;
}
