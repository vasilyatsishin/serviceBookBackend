package com.example.serviceBookBackend.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Blob;

@Data
public class CarCreateDTO {
    private String name;
    private int odometer;
    private MultipartFile photo;
}

