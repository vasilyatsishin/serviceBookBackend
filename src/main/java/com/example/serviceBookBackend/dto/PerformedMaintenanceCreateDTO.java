package com.example.serviceBookBackend.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PerformedMaintenanceCreateDTO {
    private Integer odometer;
    private LocalDate date;
    private double price;
    private Integer carId;
    private String place;
    private String comment;
    private List<Integer> performedMaintenance;
}
