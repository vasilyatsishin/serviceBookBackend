package com.example.serviceBookBackend.repository;

import com.example.serviceBookBackend.entity.PerformedMaintenanceJobLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformedMaintenanceJobLinkRepository extends JpaRepository<PerformedMaintenanceJobLink, Integer> {
}
