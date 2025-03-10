package com.fooddelivery.chefs.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class GeocodingResponse {
    private String displayName;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
