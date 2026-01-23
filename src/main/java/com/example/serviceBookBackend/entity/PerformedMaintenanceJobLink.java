package com.example.serviceBookBackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "performed_maintenance")
@Getter
@Setter
public class PerformedMaintenanceJobLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maintenance_id", nullable = false)
    private PerformedMaintenanceEntity performedMaintenanceEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maintenance_job_id", nullable = false)
    private MaintenanceJobEntity maintenanceJobEntity;
}
