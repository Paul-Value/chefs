package com.fooddelivery.chefs.model.dto;

import lombok.Data;

@Data
public class AddressSearchRequest {
    @NotBlank
    private String query;
}
