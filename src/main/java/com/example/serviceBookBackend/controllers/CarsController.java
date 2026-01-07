package com.example.serviceBookBackend.controllers;

import com.example.serviceBookBackend.dto.CarCreateDTO;
import com.example.serviceBookBackend.dto.CarResponseDTO;
import com.example.serviceBookBackend.entity.CarEntity;
import com.example.serviceBookBackend.services.CarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/cars")
@RestController
public class CarsController {
    private final CarService carService;

    @GetMapping("/exist-cars")
        public ResponseEntity<List<CarResponseDTO>> addedCars() {
        log.info("Received request to get existing cars");
        return ResponseEntity.ok()
                .body(carService.existCars());
    }

    @PostMapping(
            value = "/create",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public void addCar(@ModelAttribute CarCreateDTO car) throws IOException {
        log.info("Received request to add car");
        carService.addCar(car);
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity<byte[]> getCarPhoto(@PathVariable Integer id) throws IOException {
        long start = System.currentTimeMillis();
        byte[] photo = carService.getPhoto(id);
        long duration = System.currentTimeMillis() - start;
        log.info("Час отримання фото з бази: {} ms", duration);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic())
                .contentType(MediaType.IMAGE_JPEG)
                .body(photo);
    }
}
