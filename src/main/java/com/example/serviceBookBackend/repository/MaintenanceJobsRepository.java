package com.example.serviceBookBackend.repository;

import com.example.serviceBookBackend.entity.MaintenanceJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceJobsRepository extends JpaRepository<MaintenanceJobEntity, Integer> {
    List<MaintenanceJobEntity> findAllByCarId(Integer carId);
}
