package com.fooddelivery.chefs.model.dto;

import com.fooddelivery.chefs.model.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderCreateResponse {
    private Long orderId;
    private OrderStatus status;
    private BigDecimal totalPrice;
}
