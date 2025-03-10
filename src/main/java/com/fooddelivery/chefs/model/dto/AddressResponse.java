package com.fooddelivery.chefs.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddressResponse {
    private String formattedAddress;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String entrance;
    private String floor;
    private String apartment;
}
