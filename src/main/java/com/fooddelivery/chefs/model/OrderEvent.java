package com.fooddelivery.chefs.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class OrderEvent {
    private Long orderId;
    private Long chefId;
    private String customerName;
    private Instant createdAt;
}
