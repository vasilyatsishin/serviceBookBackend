package com.example.serviceBookBackend.repository;

import com.example.serviceBookBackend.entity.CarEntity;
import jakarta.persistence.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<CarEntity, Integer> {
    @Query("SELECT c.photo FROM CarEntity c WHERE c.id = :id")
    byte[] getPhotoOnlyById(@Param("id") Integer id);
}
