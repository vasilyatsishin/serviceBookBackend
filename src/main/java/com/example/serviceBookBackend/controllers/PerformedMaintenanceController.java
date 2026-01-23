package com.example.serviceBookBackend.controllers;

import com.example.serviceBookBackend.dto.PerformedMaintenanceCreateDTO;
import com.example.serviceBookBackend.dto.PerformedMaintenancesResponseDTO;
import com.example.serviceBookBackend.services.PerformedMaintenanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/performed-maintenance")
@RestController
public class PerformedMaintenanceController {
    private final PerformedMaintenanceService performedMaintenanceService;

    @PostMapping("/create")
    public ResponseEntity<String> addPerformedMaintenance(@RequestBody PerformedMaintenanceCreateDTO performedMaintenanceCreateDTO) {
        log.info("Received request to add performed maintenance");
        String createdPerformedMaintenance = performedMaintenanceService.addPerformedMaintenance(performedMaintenanceCreateDTO);
        return ResponseEntity.ok()
                .body(createdPerformedMaintenance);
    }

    @GetMapping()
    public ResponseEntity<List<PerformedMaintenancesResponseDTO>> getPerformedMaintenancesByCarId(@RequestParam int carId) {
        log.info("Received request to get performed maintenances for car {}", carId);
        List<PerformedMaintenancesResponseDTO> performedMaintenances = performedMaintenanceService.getPerformedMaintenances(carId);
        return ResponseEntity.ok()
                .body(performedMaintenances);
    }
}
