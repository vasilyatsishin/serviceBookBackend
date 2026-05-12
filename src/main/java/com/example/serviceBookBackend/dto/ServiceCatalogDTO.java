package com.example.serviceBookBackend.dto;

import lombok.Data;

@Data
public class ServiceCatalogDTO {
    private Integer id;
    private String name;
    private String description;
    private double price;
}
