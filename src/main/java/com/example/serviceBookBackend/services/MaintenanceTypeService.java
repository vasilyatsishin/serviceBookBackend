package com.example.serviceBookBackend.services;

import com.example.serviceBookBackend.dto.MaintenanceTypeCreateDTO;
import com.example.serviceBookBackend.dto.view.NextMaintenanceView;
import com.example.serviceBookBackend.entity.CarEntity;
import com.example.serviceBookBackend.entity.MaintenanceJobEntity;
import com.example.serviceBookBackend.constants.CacheKeys;
import com.example.serviceBookBackend.repository.CarRepository;
import com.example.serviceBookBackend.repository.MaintenanceJobsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaintenanceTypeService {
    private final MaintenanceJobsRepository maintenanceJobsRepository;
    private final CarRepository carRepository;

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheKeys.NEXT_MAINTENANCES_LIST, key = "#dto.carId"),
            @CacheEvict(value = CacheKeys.CARS_LIST, allEntries = true)
    })
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

    @Cacheable(value = CacheKeys.NEXT_MAINTENANCES_LIST, key = "#carId")
    public List<NextMaintenanceView> getNextMaintenances(Integer carId) {
        try {
            List<NextMaintenanceView> nextMaintenances = maintenanceJobsRepository.findAllNextMaintenancesByCarId(carId);
            log.info("Found and returned {} next maintenance jobs", nextMaintenances.size());
            return nextMaintenances;
        } catch (Exception e) {
            log.error("Error while getting next maintenance jobs: {}", e.getMessage());
            throw new RuntimeException("Помилка під час отримання списку типів обслуговування: " + e.getMessage());
        }
    }

    private MaintenanceJobEntity createEntity(CarEntity car, MaintenanceTypeCreateDTO dto) {
        MaintenanceJobEntity entity = new MaintenanceJobEntity();
        entity.setName(dto.getName());
        entity.setFrequency(dto.getInterval());
        entity.setCar(car);
        entity.setRegular(dto.isRegular());
        return entity;
    }

}