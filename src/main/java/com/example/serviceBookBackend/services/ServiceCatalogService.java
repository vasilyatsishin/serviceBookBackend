package com.example.serviceBookBackend.services;

import com.example.serviceBookBackend.dto.ServiceCatalogCreateDTO;
import com.example.serviceBookBackend.dto.ServiceCatalogDTO;
import com.example.serviceBookBackend.entity.ServiceCatalogEntity;
import com.example.serviceBookBackend.exceptions.CustomException;
import com.example.serviceBookBackend.repository.ServiceCatalogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceCatalogService {

    private final ServiceCatalogRepository repository;

    public List<ServiceCatalogDTO> getAll() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    public ServiceCatalogDTO create(ServiceCatalogCreateDTO dto) {
        ServiceCatalogEntity entity = new ServiceCatalogEntity();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        return toDTO(repository.save(entity));
    }

    public ServiceCatalogDTO update(Integer id, ServiceCatalogCreateDTO dto) {
        ServiceCatalogEntity entity = repository.findById(id)
                .orElseThrow(() -> new CustomException("Послугу не знайдено", HttpStatus.NOT_FOUND));
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        return toDTO(repository.save(entity));
    }

    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new CustomException("Послугу не знайдено", HttpStatus.NOT_FOUND);
        }
        repository.deleteById(id);
    }

    private ServiceCatalogDTO toDTO(ServiceCatalogEntity e) {
        ServiceCatalogDTO dto = new ServiceCatalogDTO();
        dto.setId(e.getId());
        dto.setName(e.getName());
        dto.setDescription(e.getDescription());
        dto.setPrice(e.getPrice());
        return dto;
    }
}
