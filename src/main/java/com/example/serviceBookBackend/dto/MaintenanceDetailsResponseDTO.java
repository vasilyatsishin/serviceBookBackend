package com.example.serviceBookBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MaintenanceDetailsResponseDTO {
    private List<MaintenanceDetailsItemDTO> works;
    private double totalPrice;
}
