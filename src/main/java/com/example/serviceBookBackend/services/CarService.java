package com.example.serviceBookBackend.services;

import com.example.serviceBookBackend.dto.CarCreateDTO;
import com.example.serviceBookBackend.dto.CarResponseDTO;
import com.example.serviceBookBackend.entity.CarEntity;
import com.example.serviceBookBackend.constants.CacheKeys;
import com.example.serviceBookBackend.entity.UserEntity;
import com.example.serviceBookBackend.exceptions.CustomException;
import com.example.serviceBookBackend.repository.CarRepository;
import com.example.serviceBookBackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
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
    private final JWTService jWTService;
    private final UserRepository userRepository;

    // Очищуємо список саме для цього юзера
    @CacheEvict(value = CacheKeys.CARS_LIST, key = "#root.target.currentUserId")
    public void addCar(CarCreateDTO car) throws IOException {
        Integer userId = jWTService.getCurrentUserId();

        if (car.getPhoto() == null) {
            throw new RuntimeException("Фото обовʼязкове");
        }

        // Стиснення фото (код залишаємо без змін)
        byte[] photoBytes = car.getPhoto().getBytes();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(new ByteArrayInputStream(photoBytes))
                .size(800, 600)
                .outputFormat("jpg")
                .outputQuality(0.75)
                .toOutputStream(outputStream);

        UserEntity user = userRepository.findReferenceById(userId);

        CarEntity carEntity = new CarEntity();
        carEntity.setName(car.getName());
        carEntity.setOdometer(car.getOdometer());
        carEntity.setPhoto(outputStream.toByteArray());
        carEntity.setUser(user);

        carRepository.save(carEntity);
        log.info("Car added for user: {}", userId);
    }

    // Кеш тепер розділений по userId
    @Cacheable(value = CacheKeys.CARS_LIST, key = "#root.target.currentUserId")
    public List<CarResponseDTO> existCars() {
        Integer userId = jWTService.getCurrentUserId();
        log.info("Getting cars from DB for user: {}", userId);

        // ВАЖЛИВО: використовуй findAllByUserId, а не просто findAll
        List<CarEntity> cars = carRepository.findAllByUserId(userId);

        return cars.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheKeys.CAR_BY_ID, key = "#carId"),
            @CacheEvict(value = CacheKeys.CARS_LIST, key = "#root.target.currentUserId"),
            @CacheEvict(value = CacheKeys.NEXT_MAINTENANCES_LIST, key = "#carId"),
    })
    public String updateOdometer(Integer carId, Integer newOdometer) {
        Integer userId = jWTService.getCurrentUserId();

        // Знаходимо машину і перевіряємо власника
        CarEntity car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Автомобіль не знайдено"));

        if (!car.getUser().getId().equals(userId)) {
            throw new CustomException("Доступ заборонено", HttpStatus.FORBIDDEN);
        }

        if (newOdometer < car.getOdometer()) {
            throw new CustomException("Новий пробіг не може бути меншим", HttpStatus.BAD_REQUEST);
        }

        car.setOdometer(newOdometer);
        return "Пробіг оновлено";
    }

    @Cacheable(value = CacheKeys.CAR_BY_ID, key = "#id")
    public CarResponseDTO getCarById(Integer id) {
        Integer userId = jWTService.getCurrentUserId();

        CarEntity car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Автомобіль не знайдено"));

        // Перевірка власності (якщо це не публічне фото)
        if (!car.getUser().getId().equals(userId)) {
            throw new CustomException("Доступ заборонено", HttpStatus.FORBIDDEN);
        }

        return convertToDTO(car);
    }

    // Допоміжний метод для отримання ID в анотаціях кешу
    public Integer getCurrentUserId() {
        return jWTService.getCurrentUserId();
    }

    @Cacheable(value = CacheKeys.CAR_PHOTOS, key = "#id")
    public byte[] getPhoto(Integer id) {
        try {
            return carRepository.getPhotoOnlyById(id);
        } catch (RuntimeException e) {
            log.error("Error while getting photo", e);
            throw new RuntimeException("Помилка отримання фото");
        }
    }

    private CarResponseDTO convertToDTO(CarEntity car) {
        CarResponseDTO dto = new CarResponseDTO();
        dto.setId(car.getId());
        dto.setName(car.getName());
        dto.setOdometer(car.getOdometer());
        dto.setPhotoUrl("/api/cars/" + car.getId() + "/photo");
        return dto;
    }
}