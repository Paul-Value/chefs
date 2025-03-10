package com.fooddelivery.chefs.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChefResponse {
    private Long chefId;
    private String photoUrl;
    private String description;
    private String phone;
    private boolean isWorking;
    private List<FoodResponse> foods;
}
