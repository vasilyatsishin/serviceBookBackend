package com.example.serviceBookBackend.services;

import com.example.serviceBookBackend.dto.PerformedMaintenanceCreateDTO;
import com.example.serviceBookBackend.dto.PerformedMaintenancesResponseDTO;
import com.example.serviceBookBackend.entity.CarEntity;
import com.example.serviceBookBackend.entity.MaintenanceJobEntity;
import com.example.serviceBookBackend.entity.PerformedMaintenanceEntity;
import com.example.serviceBookBackend.entity.PerformedMaintenanceJobLink;
import com.example.serviceBookBackend.constants.CacheKeys;
import com.example.serviceBookBackend.repository.CarRepository;
import com.example.serviceBookBackend.repository.MaintenanceJobsRepository;
import com.example.serviceBookBackend.repository.PerformedMaintenanceJobLinkRepository;
import com.example.serviceBookBackend.repository.PerformedMaintenanceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PerformedMaintenanceService {
    private final PerformedMaintenanceRepository performedMaintenanceRepository;
    private final CarRepository carRepository;
    private final MaintenanceJobsRepository maintenanceJobsRepository;
    private final PerformedMaintenanceJobLinkRepository linkRepository;

    @Caching(evict = {
            @CacheEvict(value = CacheKeys.CAR_BY_ID, key = "#dto.carId"),
            @CacheEvict(value = CacheKeys.MAINTENANCE_LIST, key = "#dto.carId"),
            @CacheEvict(value = CacheKeys.NEXT_MAINTENANCES_LIST, key = "#dto.carId"),
            @CacheEvict(value = CacheKeys.CARS_LIST, allEntries = true)
    })
    @Transactional
    public String addPerformedMaintenance(PerformedMaintenanceCreateDTO dto) {
        try {
            CarEntity car = carRepository.findById(dto.getCarId())
                    .orElseThrow(() -> new RuntimeException("Автомобіль не знайдено"));

            PerformedMaintenanceEntity performedMaintenance = new PerformedMaintenanceEntity();
            performedMaintenance.setPlace(dto.getPlace());
            performedMaintenance.setOdometer(dto.getOdometer());
            performedMaintenance.setCar(car);
            performedMaintenance.setDate(dto.getDate());
            performedMaintenance.setComment(dto.getComment());
            performedMaintenance.setPrice(dto.getPrice());

            final PerformedMaintenanceEntity savedMaintenance = performedMaintenanceRepository.save(performedMaintenance);

            for (Integer jobId : dto.getPerformedMaintenance()) {
                Optional<MaintenanceJobEntity> optionalJob = maintenanceJobsRepository.findById(jobId);
                if (optionalJob.isEmpty()) {
                    throw new RuntimeException("Не знайдено тип обслуговування з id=" + jobId);
                }
                MaintenanceJobEntity jobEntity = optionalJob.get();
                log.info("Saving link: maintenanceId={}, jobId={}", savedMaintenance.getId(), jobEntity.getId());

                PerformedMaintenanceJobLink link = new PerformedMaintenanceJobLink();
                link.setMaintenanceJobEntity(jobEntity);
                link.setPerformedMaintenanceEntity(savedMaintenance);
                linkRepository.save(link);
            }

            car.setOdometer(dto.getOdometer());

            carRepository.save(car);

            return "Успішно створено запис про обслуговування";
        } catch (Exception e) {
            log.error("Error while creating new maintenance: {}", e.getMessage());
            throw new RuntimeException("Помилка під час створення запису: " + e.getMessage());
        }
    }

    @Cacheable(value = CacheKeys.MAINTENANCE_LIST, key = "#carId")
    public List<PerformedMaintenancesResponseDTO> getPerformedMaintenances(int carId) {
        try {
            log.info("Getting performed maintenances for car: {}", carId);
            List<PerformedMaintenanceEntity> performedMaintenances = performedMaintenanceRepository.findAllByCarId((carId));
            List<PerformedMaintenancesResponseDTO> performedMaintenancesResponseDTOS = performedMaintenances.stream().map(performedMaintenance -> {
                PerformedMaintenancesResponseDTO dto = new PerformedMaintenancesResponseDTO();
                dto.setId(performedMaintenance.getId());
                dto.setPrice(performedMaintenance.getPrice());
                dto.setComment(performedMaintenance.getComment());
                dto.setPlace(performedMaintenance.getPlace());
                dto.setOdometer(performedMaintenance.getOdometer());
                dto.setDate(performedMaintenance.getDate());
                return dto;
            }).toList();
            log.info("Returned {} maintenances for car: {}", performedMaintenancesResponseDTOS.size(), carId);
            return performedMaintenancesResponseDTOS;
        } catch (Exception e) {
            log.error("Error while getting performed maintenances: {}", e.getMessage());
            throw new RuntimeException("Помилка під час отримання проведених технічних оглядів: " + e.getMessage());
        }
    }
}
