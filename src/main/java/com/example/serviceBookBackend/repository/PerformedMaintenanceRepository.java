package com.example.serviceBookBackend.repository;

import com.example.serviceBookBackend.entity.CarEntity;
import com.example.serviceBookBackend.entity.MaintenanceJobEntity;
import com.example.serviceBookBackend.entity.PerformedMaintenanceEntity;
import jakarta.persistence.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformedMaintenanceRepository extends JpaRepository<PerformedMaintenanceEntity, Integer> {
    List<PerformedMaintenanceEntity> findAllByCarId(Integer carId);
}
