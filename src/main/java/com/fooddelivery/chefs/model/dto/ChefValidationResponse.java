package com.fooddelivery.chefs.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChefValidationResponse {
    private Long chefId;
    private String message;
}
