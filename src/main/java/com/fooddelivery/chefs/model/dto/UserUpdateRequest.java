package com.fooddelivery.chefs.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


@Data
public class UserUpdateRequest {
    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @Pattern(regexp = "^\\+7\\d{10}$", message = "Некорректный формат телефона")
    private String phone;
}
