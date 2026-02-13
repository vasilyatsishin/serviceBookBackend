package com.example.serviceBookBackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserDTO {
    @NotBlank(message = "Email не може бути порожнім")
    @Email(message = "Невірний формат email")
    @Size(max = 100, message = "Email занадто довгий")
    private String email;

    @NotBlank(message = "Пароль не може бути порожнім")
    @Size(min = 5, max = 16, message = "Пароль має бути від 8 до 16 символів")
    // Можна додати @Pattern для латини та цифр, як ми робили на фронті
    private String password;

    @NotBlank(message = "Ім'я не може бути порожнім")
    @Size(min = 2, max = 100, message = "Ім'я має бути від 2 до 100 символів")
    private String name;
}
