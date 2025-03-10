package com.fooddelivery.chefs.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressSearchRequest {
    @NotBlank(message = "Запрос не может быть пустым")
    private String query;
}
