package com.example.serviceBookBackend.services;

import com.example.serviceBookBackend.dto.MaintenanceTypeCreateDTO;
import com.example.serviceBookBackend.dto.MaintenanceTypeResponseDTO;
import com.example.serviceBookBackend.entity.CarEntity;
import com.example.serviceBookBackend.entity.MaintenanceJobEntity;
import com.example.serviceBookBackend.repository.CarRepository;
import com.example.serviceBookBackend.repository.MaintenanceJobsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaintenanceTypeService {
    private final MaintenanceJobsRepository maintenanceJobsRepository;
    private final CarRepository carRepository;

    @Transactional
    @CacheEvict(allEntries = true, value = "maintenanceTypesList")
    public String addMaintenanceType(MaintenanceTypeCreateDTO dto) {
        try {
            if (dto.isApplyToAllCars()) {
                List<CarEntity> allCars = carRepository.findAll();

                List<MaintenanceJobEntity> jobsToSave = allCars.stream()
                        .map(car -> createEntity(car, dto))
                        .toList();

                maintenanceJobsRepository.saveAll(jobsToSave);
                log.info("Saved maintenance jobs for all {} cars", allCars.size());
            } else {
                CarEntity car = carRepository.findById(dto.getCarId())
                        .orElseThrow(() -> new RuntimeException("Автомобіль не знайдено"));

                maintenanceJobsRepository.save(createEntity(car, dto));
                log.info("Saved maintenance job for car ID: {}", dto.getCarId());
            }

            return "Тип обслуговування успішно створено";
        } catch (Exception e) {
            log.error("Error while saving maintenance job: {}", e.getMessage());
            throw new RuntimeException("Помилка під час створення: " + e.getMessage());
        }
    }

    private MaintenanceJobEntity createEntity(CarEntity car, MaintenanceTypeCreateDTO dto) {
        MaintenanceJobEntity entity = new MaintenanceJobEntity();
        entity.setName(dto.getName());
        entity.setFrequency(dto.getInterval());
        entity.setCar(car);
        return entity;
    }

    @Cacheable(value = "maintenanceTypesList")
    public List<MaintenanceTypeResponseDTO> getMaintenanceTypesList(Integer carId) {
        try {
            List<MaintenanceJobEntity> maintenanceJobs = maintenanceJobsRepository.findAllByCarId(carId);
            log.info("Found and returned {} maintenance jobs", maintenanceJobs.size());
            return maintenanceJobs.stream()
                    .map(this::convertToDTO)
                    .toList();
        } catch (Exception e) {
            log.error("Error while getting maintenance jobs list: {}", e.getMessage());
            throw new RuntimeException("Помилка під час отримання списку типів обслуговування: " + e.getMessage());
        }
    }

    private MaintenanceTypeResponseDTO convertToDTO(MaintenanceJobEntity entity) {
        MaintenanceTypeResponseDTO dto = new MaintenanceTypeResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setFrequency(entity.getFrequency());
        dto.setCarId(entity.getCar().getId());
        return dto;
    }
}