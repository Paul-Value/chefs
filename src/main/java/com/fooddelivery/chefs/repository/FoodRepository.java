package com.fooddelivery.chefs.repository;

import com.fooddelivery.chefs.model.Food;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, Long> {
}
