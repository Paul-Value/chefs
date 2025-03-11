package com.fooddelivery.chefs.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderPriceResponse {
    private BigDecimal totalPrice;
}
