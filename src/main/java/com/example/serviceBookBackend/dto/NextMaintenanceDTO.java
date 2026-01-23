package com.example.serviceBookBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NextMaintenanceDTO {
    private Integer job_id;
    private String job_name;
    private Integer frequency;
    private Integer current_car_odometer;
    private Integer last_performed_odometer;
    private Integer km_remaining;
}
