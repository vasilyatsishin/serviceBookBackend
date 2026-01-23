package com.example.serviceBookBackend.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PerformedMaintenancesResponseDTO {
    private LocalDate date;
    private int odometer;
    private String place;
    private double price;
    private String comment;
    private int id;
}
