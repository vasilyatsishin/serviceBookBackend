package com.example.serviceBookBackend.dto.view;

public interface NextMaintenanceView {
    Integer getJobId();
    String getJobName();
    Integer getFrequency();
    Integer getCurrentCarOdometer();
    Integer getLastPerformedOdometer();
    Integer getKmRemaining();
    Boolean getIsRegular();
}

