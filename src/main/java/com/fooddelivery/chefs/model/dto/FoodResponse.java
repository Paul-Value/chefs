package com.fooddelivery.chefs.model.dto;

import com.fooddelivery.chefs.model.Ingredient;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class FoodResponse {
    private Long foodId;
    private String name;
    private String description;
    private List<Ingredient> ingredients;
    private BigDecimal price;
    private String photoUrl;
}
