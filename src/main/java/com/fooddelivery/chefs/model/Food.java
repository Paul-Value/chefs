package com.fooddelivery.chefs.model;

import com.fooddelivery.chefs.model.dto.FoodResponse;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Table(name = "food")
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long foodId;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "chef_id", nullable = false)
    private Chef chef;

    @ManyToMany
    @JoinTable(
            name = "food_ingredient",
            joinColumns = @JoinColumn(name = "food_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private List<Ingredient> ingredients;

    private FoodResponse convertFoodToResponse(Food food) {
        return FoodResponse.builder()
                .foodId(food.getFoodId())
                .name(food.getName())
                .description(food.getDescription())
                .price(food.getPrice())
                .photoUrl(food.getPhotoUrl())
                .build();
    }
}
