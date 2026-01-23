package com.example.serviceBookBackend.controllers;

import com.example.serviceBookBackend.dto.MaintenanceTypeCreateDTO;
import com.example.serviceBookBackend.dto.MaintenanceTypeResponseDTO;
import com.example.serviceBookBackend.dto.view.NextMaintenanceView;
import com.example.serviceBookBackend.services.MaintenanceTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/maintenance-jobs")
@RestController
public class MaintenanceController {
    private final MaintenanceTypeService maintenanceTypeService;

    @PostMapping("/create")
    public ResponseEntity<String> addMaintenanceType(@RequestBody MaintenanceTypeCreateDTO maintenanceTypeCreateDTO) {
        log.info("Received request to add maintenance type");
        String createdMaintenanceType = maintenanceTypeService.addMaintenanceType(maintenanceTypeCreateDTO);
        return ResponseEntity.ok()
                .body(createdMaintenanceType);
    }

    @GetMapping("/next-maintenance")
    public ResponseEntity<List<NextMaintenanceView>> getAllNextMaintenanceJobs(@RequestParam Integer carId) {
        log.info("Received request to get next maintenances");
        List<NextMaintenanceView> nextMaintenancesList = maintenanceTypeService.getNextMaintenances(carId);
        return ResponseEntity.ok()
                .body(nextMaintenancesList);
    }
}
