package com.example.serviceBookBackend.services;

import com.example.serviceBookBackend.dto.PerformedMaintenanceCreateDTO;
import com.example.serviceBookBackend.dto.PerformedMaintenancesResponseDTO;
import com.example.serviceBookBackend.entity.CarEntity;
import com.example.serviceBookBackend.entity.PerformedMaintenanceEntity;
import com.example.serviceBookBackend.entity.PerformedMaintenanceJobLink;
import com.example.serviceBookBackend.entity.ServiceCatalogEntity;
import com.example.serviceBookBackend.constants.CacheKeys;
import com.example.serviceBookBackend.exceptions.CustomException;
import com.example.serviceBookBackend.repository.CarRepository;
import com.example.serviceBookBackend.repository.PerformedMaintenanceJobLinkRepository;
import com.example.serviceBookBackend.repository.PerformedMaintenanceRepository;
import com.example.serviceBookBackend.repository.ServiceCatalogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PerformedMaintenanceService {
    private final PerformedMaintenanceRepository performedMaintenanceRepository;
    private final CarRepository carRepository;
    private final ServiceCatalogRepository serviceCatalogRepository;
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

            if (dto.getPerformedCatalogIds() != null) {
                for (Integer catalogId : dto.getPerformedCatalogIds()) {
                    ServiceCatalogEntity catalogItem = serviceCatalogRepository.findById(catalogId)
                            .orElseThrow(() -> new RuntimeException("Не знайдено послугу з id=" + catalogId));

                    log.info("Saving catalog link: maintenanceId={}, catalogId={}", savedMaintenance.getId(), catalogId);

                    PerformedMaintenanceJobLink link = new PerformedMaintenanceJobLink();
                    link.setCatalogEntity(catalogItem);
                    link.setPerformedMaintenanceEntity(savedMaintenance);
                    linkRepository.save(link);
                }
            }

            car.setOdometer(dto.getOdometer());

            carRepository.save(car);

            return "Успішно створено запис про обслуговування";
        } catch (Exception e) {
            log.error("Error while creating new maintenance: {}", e.getMessage());
            throw new RuntimeException("Помилка під час створення запису: " + e.getMessage());
        }
    }

    @Transactional
    @CacheEvict(value = CacheKeys.MAINTENANCE_LIST, key = "#carId")
    public String payMaintenance(Integer maintenanceId, Integer carId) {
        PerformedMaintenanceEntity entity = performedMaintenanceRepository.findById(maintenanceId)
                .orElseThrow(() -> new CustomException("Запис обслуговування не знайдено", HttpStatus.NOT_FOUND));

        if (entity.isPaid()) {
            throw new CustomException("Послуга вже оплачена", HttpStatus.BAD_REQUEST);
        }

        entity.setPaid(true);
        performedMaintenanceRepository.save(entity);
        log.info("Maintenance {} marked as paid", maintenanceId);
        return "Оплату успішно здійснено";
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
                dto.setPaid(performedMaintenance.isPaid());
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
