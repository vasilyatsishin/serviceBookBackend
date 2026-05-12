package com.example.serviceBookBackend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ServiceCatalogCreateDTO {
    @NotBlank
    private String name;
    private String description;
    @Min(0)
    private double price;
}
