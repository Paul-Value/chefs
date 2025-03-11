package com.fooddelivery.chefs.model.dto;

import com.fooddelivery.chefs.model.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long orderId;
    private String chefName;
    private String chefPhone;
    private OrderStatus status;
    private ZonedDateTime createdAt;
    private BigDecimal totalPrice;
    private List<OrderItemResponse> items;
}
