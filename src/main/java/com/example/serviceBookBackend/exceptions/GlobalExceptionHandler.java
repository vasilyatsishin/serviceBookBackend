package com.example.serviceBookBackend.exceptions;

import com.example.serviceBookBackend.dto.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponseDTO> buildResponse(HttpStatus status, String message) {
        return ResponseEntity
                .status(status)
                .body(new ErrorResponseDTO(message, System.currentTimeMillis()));
    }

    // 1. Коли не знайшли запис (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // 2. Помилки бази даних (503)
    @ExceptionHandler(org.springframework.dao.DataAccessException.class)
    public ResponseEntity<ErrorResponseDTO> handleDatabaseError(org.springframework.dao.DataAccessException ex) {
        log.error("Database error: ", ex);
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, "Проблема зі з'єднанням з базою даних");
    }

    // 3. Помилки валідації (400)
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Некоректні дані у запиті");
    }

    // 4. Загальна помилка (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleEverythingElse(Exception ex) {
        log.error("Unknown error: ", ex);
        // Тут ми віддаємо ex.getMessage(), щоб ти бачив причину на фронті
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Критична помилка: " + ex.getMessage());
    }
}