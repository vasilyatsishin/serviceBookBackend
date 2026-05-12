package com.example.serviceBookBackend.controllers;

import com.example.serviceBookBackend.dto.ServiceCatalogCreateDTO;
import com.example.serviceBookBackend.dto.ServiceCatalogDTO;
import com.example.serviceBookBackend.services.ServiceCatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/services-catalog")
@RestController
public class ServiceCatalogController {

    private final ServiceCatalogService serviceCatalogService;

    @GetMapping
    public ResponseEntity<List<ServiceCatalogDTO>> getAll() {
        return ResponseEntity.ok(serviceCatalogService.getAll());
    }

    @PostMapping
    public ResponseEntity<ServiceCatalogDTO> create(@Valid @RequestBody ServiceCatalogCreateDTO dto) {
        log.info("Creating catalog item: {}", dto.getName());
        return ResponseEntity.ok(serviceCatalogService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceCatalogDTO> update(@PathVariable Integer id,
                                                     @Valid @RequestBody ServiceCatalogCreateDTO dto) {
        log.info("Updating catalog item: {}", id);
        return ResponseEntity.ok(serviceCatalogService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        log.info("Deleting catalog item: {}", id);
        serviceCatalogService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
