package com.fooddelivery.chefs.model;

import lombok.Data;

import java.time.Instant;

@Data
public class OrderEvent {
    private Long orderId;
    private Long chefId;
    private String customerName;
    private Instant createdAt;
}
