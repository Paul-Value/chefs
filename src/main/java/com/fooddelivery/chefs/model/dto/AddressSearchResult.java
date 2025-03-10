package com.fooddelivery.chefs.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddressSearchResult {
    private String formattedAddress;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String placeId;
}
