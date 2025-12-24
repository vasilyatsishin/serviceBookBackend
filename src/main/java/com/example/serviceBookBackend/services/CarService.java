package com.example.serviceBookBackend.services;

import com.example.serviceBookBackend.dto.CarCreateDTO;
import com.example.serviceBookBackend.dto.CarResponseDTO;
import com.example.serviceBookBackend.entity.CarEntity;
import com.example.serviceBookBackend.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.cache.annotation.Cacheable;
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

    public void addCar(CarCreateDTO car) throws IOException {
        byte[] photoBytes = car.getPhoto() != null
                ? car.getPhoto().getBytes()
                : null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(new ByteArrayInputStream(photoBytes))
                .size(800, 600)
                .outputFormat("jpg")
                .outputQuality(0.75) // 75% якості зазвичай достатньо
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

    @Cacheable(value = "carsList")
    public List<CarResponseDTO> existCars() {
        try {
            List<CarEntity> cars = carRepository.findAll();

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

    @Cacheable(value = "carPhotos", key = "#id")
    public byte[] getPhoto(Integer id) throws IOException {
        try {
            return carRepository.getPhotoOnlyById(id);
        } catch (RuntimeException e) {
            log.error("Error while getting photo", e);
            throw new RuntimeException("Помилка отримання фото");
        }
    }
}
