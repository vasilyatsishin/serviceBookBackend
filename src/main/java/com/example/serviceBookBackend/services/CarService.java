package com.example.serviceBookBackend.services;

import com.example.serviceBookBackend.dto.CarCreateDTO;
import com.example.serviceBookBackend.dto.CarResponseDTO;
import com.example.serviceBookBackend.entity.CarEntity;
import com.example.serviceBookBackend.constants.CacheKeys;
import com.example.serviceBookBackend.repository.CarRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;

    @CacheEvict(
            value = {CacheKeys.CARS_LIST, CacheKeys.CAR_PHOTOS},
            allEntries = true
    )
    public void addCar(CarCreateDTO car) throws IOException {
        if (car.getPhoto() == null) {
            throw new RuntimeException("Фото обовʼязкове");
        }
        byte[] photoBytes = car.getPhoto() != null ? car.getPhoto().getBytes() : null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(new ByteArrayInputStream(photoBytes)).size(800, 600).outputFormat("jpg").outputQuality(0.75) // 75% якості зазвичай достатньо
                .toOutputStream(outputStream);

        byte[] compressed = outputStream.toByteArray();
        CarEntity carEntity = new CarEntity();
        carEntity.setName(car.getName());
        carEntity.setOdometer(car.getOdometer());
        carEntity.setPhoto(compressed);
        try {
            carRepository.save(carEntity);
            log.info("Car added successfully");
        } catch (RuntimeException e) {
            log.error("Error while saving car", e);
            throw new RuntimeException("Помилка додавання автомобіля");
        }
    }

    @Cacheable(value = CacheKeys.CARS_LIST)
    public List<CarResponseDTO> existCars() {
        try {
            log.info("Getting cars from database...");
            List<CarEntity> cars = carRepository.findAll();

            log.info("Cars returned from database");
            return cars.stream().map(car -> {
                CarResponseDTO dto = new CarResponseDTO();
                dto.setId(car.getId());
                dto.setName(car.getName());
                dto.setOdometer(car.getOdometer());
                dto.setPhotoUrl("/api/cars/" + car.getId() + "/photo");
                return dto;
            }).collect(Collectors.toList());
        } catch (RuntimeException e) {
            log.error("Error while getting cars", e);
            throw new RuntimeException("Помилка отримання автомобілів");
        }
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheKeys.CAR_BY_ID, key = "#carId"),
            @CacheEvict(value = CacheKeys.CARS_LIST, allEntries = true),
            @CacheEvict(value = CacheKeys.NEXT_MAINTENANCES_LIST, key = "#carId"),
    })
    public String updateOdometer(Integer carId, Integer newOdometer) {
        log.info("Updating odometer for car: {}", carId);

        CarEntity car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Автомобіль не знайдено"));

        car.setOdometer(newOdometer);

        return "Пробіг оновлено успішно";
    }

    @Cacheable(value = CacheKeys.CAR_BY_ID, key = "#id")
    public CarResponseDTO getCarById(Integer id) {
        try {
            log.info("Getting car from database...");
            CarEntity car = carRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Автомобіль не знайдено"));


            log.info("Car returned from database");
            CarResponseDTO dto = new CarResponseDTO();
            dto.setId(car.getId());
            dto.setName(car.getName());
            dto.setOdometer(car.getOdometer());
            dto.setPhotoUrl("/api/cars/" + car.getId() + "/photo");
            return dto;

        } catch (RuntimeException e) {
            log.error("Error while getting car by id", e);
            throw new RuntimeException("Помилка отримання автомобіля");
        }
    }

    @Cacheable(value = "carPhotos", key = "#id")
    public byte[] getPhoto(Integer id) {
        try {
            return carRepository.getPhotoOnlyById(id);
        } catch (RuntimeException e) {
            log.error("Error while getting photo", e);
            throw new RuntimeException("Помилка отримання фото");
        }
    }
}
