package com.fooddelivery.chefs.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OrderPriceRequest {
    @NotEmpty(message = "Список блюд не может быть пустым")
    private List<OrderItemRequest> items;
}
