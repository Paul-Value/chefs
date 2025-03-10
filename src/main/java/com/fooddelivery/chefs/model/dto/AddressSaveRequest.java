package com.fooddelivery.chefs.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressSaveRequest {
    @NotBlank(message = "Place ID обязателен")
    private String placeId;
    private String entrance;
    private String floor;
    private String apartment;
}
