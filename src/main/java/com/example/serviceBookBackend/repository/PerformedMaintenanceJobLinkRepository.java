package com.example.serviceBookBackend.repository;

import com.example.serviceBookBackend.entity.PerformedMaintenanceJobLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformedMaintenanceJobLinkRepository extends JpaRepository<PerformedMaintenanceJobLink, Integer> {
    List<PerformedMaintenanceJobLink> findByPerformedMaintenanceEntityId(Integer maintenanceId);
}
