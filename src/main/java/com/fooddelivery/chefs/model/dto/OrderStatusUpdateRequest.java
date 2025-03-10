package com.fooddelivery.chefs.model.dto;

import com.fooddelivery.chefs.model.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderStatusUpdateRequest {
    @NotNull(message = "Статус обязателен")
    private OrderStatus status;
}
