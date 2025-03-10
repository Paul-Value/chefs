package com.fooddelivery.chefs.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderItemRequest {
    @NotNull(message = "ID блюда обязательно")
    private Long foodId;
    @Min(value = 1, message = "Количество должно быть не меньше 1")
    private Integer quantity;
}
