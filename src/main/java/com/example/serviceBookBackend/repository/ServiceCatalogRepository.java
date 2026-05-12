package com.example.serviceBookBackend.repository;

import com.example.serviceBookBackend.entity.ServiceCatalogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceCatalogRepository extends JpaRepository<ServiceCatalogEntity, Integer> {
}
